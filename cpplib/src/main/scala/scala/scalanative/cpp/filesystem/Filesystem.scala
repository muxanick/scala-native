package scala.scalanative.cpp.filesystem

import scalanative.unsafe._
import scala.scalanative.cpp._
import scala.scalanative.cpp.ios._
import scala.collection.immutable.{Stream => SStream}

object Filesystem {
    def is_file(path: String): Boolean = Zone { implicit z =>
        FilesystemNative.is_file_str(toCString(path))
    }
    def is_file(path: Path): Boolean = FilesystemNative.is_file(path.getNativeObject())

    def is_directory(path: String): Boolean = Zone { implicit z =>
        FilesystemNative.is_directory_str(toCString(path))
    }
    def is_directory(path: Path): Boolean = FilesystemNative.is_directory(path.getNativeObject())

    def directory_iterator(path: String): SStream[DirectoryEntry] = {
        directory_iterator(Path(path).autoClose[Path]())
    }
    def directory_iterator(path: Path): SStream[DirectoryEntry] = {
        val obj = FilesystemNative.directory_iterator(path.getNativeObject())
        toScalaStream(true, new DirectoryIterator {
            def increment(): Boolean = FilesystemNative.directory_iterator_increment(obj)
            def value(): DirectoryEntry = new DirectoryEntry(FilesystemNative.directory_iterator_value(obj))
        })
    }
    
    def file_size(path: Path): Streamsize = {
        FilesystemNative.file_size(path.getNativeObject())
    }

    def file_size(filename: String): Streamsize = {
        file_size(Path(filename))
    }

    def exists(path: Path): Boolean = {
        FilesystemNative.exists(path.getNativeObject())
    }

    def exists(filename: String): Boolean = {
        exists(Path(filename))
    }

    private trait DirectoryIterator {
        def increment(): Boolean
        def value(): DirectoryEntry
    }

    private[this] def toScalaStream(first: Boolean, it: DirectoryIterator): SStream[DirectoryEntry] = {
        if (first || it.increment())
            it.value() #:: toScalaStream(false, it)
        else
            SStream.empty
    }
}

@extern
private[cpp] object FilesystemNative
{
    @name("scalanative_cpp_filesystem_is_file_str")
    def is_file_str(filename: CString): CBool = extern
  
    @name("scalanative_cpp_filesystem_is_directory_str")
    def is_directory_str(filename: CString): CBool = extern

    @name("scalanative_cpp_filesystem_is_file")
    def is_file(path: NativeObject): CBool = extern
  
    @name("scalanative_cpp_filesystem_is_directory")
    def is_directory(path: NativeObject): CBool = extern

    @name("scalanative_cpp_filesystem_directory_iterator")
    def directory_iterator(path: NativeObject): NativeObject = extern

    @name("scalanative_cpp_filesystem_directory_iterator_increment")
    def directory_iterator_increment(it: NativeObject): CBool = extern

    @name("scalanative_cpp_filesystem_directory_iterator_value")
    def directory_iterator_value(it: NativeObject): NativeObject = extern

    @name("scalanative_cpp_filesystem_file_size")
    def file_size(it: NativeObject): Streamsize = extern

    @name("scalanative_cpp_filesystem_exists")
    def exists(it: NativeObject): CBool = extern
}