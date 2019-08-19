package scala.scalanative
package build

import java.nio.file.{Files, Path, Paths}
import java.util.Arrays
import scala.collection.JavaConverters._
import scala.util.Try
import scala.sys.process._
import scalanative.build.IO.RichPath

/** Internal utilities to interact with LLVM command-line tools. */
private[scalanative] object LLVM {

  /**
   * Unpack the `nativelib` to `workdir/lib`.
   *
   * If the same archive has already been unpacked to this location, this
   * call has no effects.
   *
   * @param nativelib The JAR to unpack.
   * @param workdir   The working directory. The nativelib will be unpacked
   *                  to `workdir/lib`.
   * @return The location where the nativelib has been unpacked, `workdir/lib`.
   */
  def unpackNativelib(nativelib: Path, workdir: Path): Path = {
    val lib         = workdir.resolve("lib")
    val jarhash     = IO.sha1(nativelib)
    val jarhashPath = lib.resolve("jarhash")
    def unpacked =
      Files.exists(lib) &&
        Files.exists(jarhashPath) &&
        Arrays.equals(jarhash, Files.readAllBytes(jarhashPath))

    if (!unpacked) {
      IO.deleteRecursive(lib)
      IO.unzip(nativelib, lib)
      IO.write(jarhashPath, jarhash)
    }

    lib
  }

  /**
   * Compile the native lib to `.o` files
   *
   * @param config       The configuration of the toolchain.
   * @param linkerResult The results from the linker.
   * @param libPath      The location where the `.o` files should be written.
   * @return `libPath`
   */
  def compileNativelib(config: Config,
                       linkerResult: linker.Result,
                       libPath: Path): Path = {
    val cpaths = IO.getAll(config.workdir, "glob:**.c").map(_.abs) ++ IO
      .getAll(config.workdir, "glob:**.S")
      .map(_.abs)
    val cpppaths = IO.getAll(config.workdir, "glob:**.cpp").map(_.abs)
    val paths    = cpaths ++ cpppaths

    // predicate to check if given file path shall be compiled
    // we only include sources of the current gc and exclude
    // all optional dependencies if they are not necessary
    val optPath = libPath.resolve("optional").abs
    val (gcPath, gcSelPath) = {
      val gcPath    = libPath.resolve("gc")
      val gcSelPath = gcPath.resolve(config.gc.name)
      (gcPath.abs, gcSelPath.abs)
    }

    config.logger.warn(s"targetTriple is ${config.targetTriple}")
    val isWindows = config.targetTriple.contains("windows")

    def include(path: String) = {
      if (path.contains(optPath)) {
        val name = Paths.get(path).toFile.getName.split("\\.").head
        true //linkerResult.links.map(_.name).contains(name)
      } else if (path.contains(gcPath)) {
        path.contains(gcSelPath)
      } else if (isWindows && path.contains("__posix__")) {
        false
      } else if (isWindows && path.contains("libunwind")) {
        false
      } else if (!isWindows && path.contains("__windows__")) {
        false
      } else if (!isWindows && path.contains("__cpp__")) {
        false
      } else {
        true
      }
    }

    // delete .o files for all excluded source files
    paths.foreach { path =>
      if (!include(path)) {
        val opath = Paths.get(path + ".o")
        if (Files.exists(opath)) {
          Files.delete(opath)
        }
      }
    }

    val abs_working_dir = config.workdir.toAbsolutePath().toString()
    // generate .o files for all included source files in parallel
    paths.par.foreach { path =>
      val opath = path + ".o"
      if (include(path) && !Files.exists(Paths.get(opath))) {
        val isCpp    = path.endsWith(".cpp")
        val compiler = if (isCpp) config.clangPP.abs else config.clang.abs
        val stdflag =
          if (isCpp) (if (isWindows) "-std=c++17" else "-std=c++11")
          else "-std=gnu11"
        val flags = stdflag +: "-stdlib=libc++" +: "-fvisibility=hidden" +: config.compileOptions
        val compilec = Seq(compiler) ++ flto(config) ++ flags ++ Seq(
          "-gfull",
          "-D_CRT_SECURE_NO_WARNINGS",
          "-Wdeprecated-declarations",
          "-I" + abs_working_dir + "\\..\\..\\..\\zlib-bin\\include",
          "-c",
          path,
          "-o",
          opath)

        config.logger.running(compilec)
        val result = Process(compilec, config.workdir.toFile) ! Logger
          .toProcessLogger(config.logger)
        if (result != 0) {
          sys.error("Failed to compile native library runtime code.")
        }
      }
    }

    libPath
  }

  /** Compile the given LL files to object files */
  def compile(config: Config, llPaths: Seq[Path]): Seq[Path] = {
    val optimizationOpt =
      config.mode match {
        case Mode.Debug       => "-O0"
        case Mode.ReleaseFast => "-O2"
        case Mode.ReleaseFull => "-O3"
      }
    val opts = optimizationOpt +: config.compileOptions

    llPaths.par
      .map { ll =>
        val apppath = ll.abs
        val outpath = apppath + ".o"
        val compile = Seq(config.clang.abs) ++ flto(config) ++ Seq(
          "-c",
          apppath,
          "-o",
          outpath) ++ opts
        config.logger.running(compile)
        Process(compile, config.workdir.toFile) ! Logger.toProcessLogger(
          config.logger)
        Paths.get(outpath)
      }
      .seq
      .toSeq
  }

  /**
   * Links a collection of `.ll` files into native binary.
   *
   * @param config       The configuration of the toolchain.
   * @param linkerResult The results from the linker.
   * @param llPaths      The list of `.ll` files to link.
   * @param nativelib    The path to the nativelib.
   * @param outpath      The path where to write the resulting binary.
   * @return `outpath`
   */
  def link(config: Config,
           linkerResult: linker.Result,
           llPaths: Seq[Path],
           nativelib: Path,
           outpath: Path): Path = {
    val isWindows = config.targetTriple.contains("windows")
    val links = {
      val srclinks = linkerResult.links.map(_.name)
      val gclinks  = config.gc.links
      // We need extra linking dependencies for:
      // * libdl for our vendored libunwind implementation.
      // * libpthread for process APIs and parallel garbage collection.
      (if (isWindows) Seq("Dbghelp.lib", "zlibd.lib") else Seq("pthread", "dl" ,"z")) ++: srclinks ++: gclinks
    }
    println("###############################################################")
    println(links)
    val abs_working_dir = config.workdir.toAbsolutePath().toString()
    println(abs_working_dir)
    val linkopts  = config.linkingOptions ++ links.map("-l" + _)
    val targetopt = Seq("-target", config.targetTriple)
    val flags = flto(config) ++ Seq(if (!isWindows) "-rdynamic" else "-g",
                                    "-gfull",
                                    "-L" + abs_working_dir + "\\..\\..\\..\\zlib-bin\\lib",
                                    "-o",
                                    outpath.abs + ".exe") ++ targetopt
    val opaths  = IO.getAll(nativelib, "glob:**.o").map(_.abs)
    val paths   = llPaths.map(_.abs) ++ opaths
    val compile = config.clangPP.abs +: (flags ++ paths ++ linkopts)
    val ltoName = lto(config).getOrElse("none")

    config.logger.time(
      s"Linking native code (${config.gc.name} gc, $ltoName lto)") {
      config.logger.running(compile)
      Process(compile, config.workdir.toFile) ! Logger.toProcessLogger(
        config.logger)
    }

    outpath
  }

  private def lto(config: Config): Option[String] =
    (config.mode, config.LTO) match {
      case (Mode.Debug, _)           => None
      case (_: Mode.Release, "none") => None
      case (_: Mode.Release, name)   => Some(name)
    }

  private def flto(config: Config): Seq[String] =
    lto(config).fold[Seq[String]] {
      Seq()
    } { name =>
      Seq(s"-flto=$name")
    }
}
