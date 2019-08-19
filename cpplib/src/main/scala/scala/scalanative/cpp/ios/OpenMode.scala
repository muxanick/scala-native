package scala.scalanative.cpp.ios
import scalanative.unsafe._

object OpenMode {
  final val app: Bitmask    = OpenModeNative.app
  final val binary: Bitmask = OpenModeNative.binary
  final val in: Bitmask     = OpenModeNative.in
  final val out: Bitmask    = OpenModeNative.out
  final val trunc: Bitmask  = OpenModeNative.trunc
  final val ate: Bitmask    = OpenModeNative.ate
}

@extern
private[cpp] object OpenModeNative {
  @name("scalanative_cpp_ios_openmode_app")
  def app: Bitmask = extern

  @name("scalanative_cpp_ios_openmode_binary")
  def binary: Bitmask = extern

  @name("scalanative_cpp_ios_openmode_in")
  def in: Bitmask = extern

  @name("scalanative_cpp_ios_openmode_out")
  def out: Bitmask = extern

  @name("scalanative_cpp_ios_openmode_trunc")
  def trunc: Bitmask = extern

  @name("scalanative_cpp_ios_openmode_ate")
  def ate: Bitmask = extern
}
