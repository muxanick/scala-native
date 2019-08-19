package scala.scalanative.cpp.filesystem

import scalanative.unsafe._
import scala.scalanative.cpp._

class Path private[cpp] (obj: NativeObject) extends CppObject(obj) {
    def string(): String = {
        val cstr = stackalloc[Byte](4096)
        val len = PathNative.path_string(getNativeObject(), cstr)
        fromCStringFast(cstr, len.toInt)
    }

    override def toString(): String = {
        "Path(" + string() + ")"
    }

    def filename: Path = new Path(PathNative.path_filename(getNativeObject()))

    def parent_path: Path = new Path(PathNative.path_parent_path(getNativeObject()))
}

object Path {
    def apply(path: String): Path = Zone { implicit z =>
        new Path(PathNative.new_path(toCString(path)))
    }
}

@extern
private[cpp] object PathNative
{
    @name("scalanative_cpp_filesystem_new_path")
    def new_path(filename: CString): NativeObject = extern

    @name("scalanative_cpp_filesystem_delete_path")
    def delete_path(filename: CString): NativeObject = extern

    @name("scalanative_cpp_filesystem_path_string")
    def path_string(path: NativeObject, buf: CString): CSize = extern

    @name("scalanative_cpp_filesystem_path_filename")
    def path_filename(path: NativeObject): NativeObject = extern

    @name("scalanative_cpp_filesystem_path_parent_path")
    def path_parent_path(path: NativeObject): NativeObject = extern
}