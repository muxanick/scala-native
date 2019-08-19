package scala.scalanative.cpp.filesystem

import scalanative.unsafe._
import scala.scalanative.annotation.stub
import scala.scalanative.cpp._

class DirectoryEntry private[cpp] (obj: NativeObject) extends CppObject(obj) {

    // Returns the full path the directory entry refers to.
    def path(): Path = {
        new Path(DirectoryEntryNative.path(getNativeObject()))
    }

    // Assigns new content to the directory entry object.
    // Sets the path to p and calls refresh to update the cached attributes.
    // If an error occurs, the values of the cached attributes are unspecified.
    @stub
    def assign(p: Path): Unit = ???

    // Changes the filename of the directory entry.
    //
    // Effectively modifies the path member by path.replace_filename(p)
    // and calls refresh to update the cached attributes.
    // If an error occurs, the values of the cached attributes are unspecified.
    //
    // This function does not commit any changes to the filesystem.
    @stub
    def replace_filename(p: Path): Unit = ??? //path().replace_filename(p);
}

@extern
private[cpp] object DirectoryEntryNative {
    @name("scalanative_cpp_filesystem_DirectoryEntry_path")
    def path(entry: NativeObject): NativeObject = extern
}
