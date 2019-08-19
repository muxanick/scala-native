package javax.tools

import java.io.{Closeable, Flushable}
import java.lang.{ClassLoader, Iterable, String}
import java.util.{Iterator, Set}
import scala.scalanative.annotation.stub

/** File manager for tools operating on Java™ programming language
 *  source and class files.  In this context, file means an
 *  abstraction of regular files and other sources of data.
 * 
 *  When constructing new JavaFileObjects, the file manager must
 *  determine where to create them.  For example, if a file manager
 *  manages regular files on a file system, it would most likely have a
 *  current/working directory to use as default location when creating
 *  or finding files.  A number of hints can be provided to a file
 *  manager as to where to create files.  Any file manager might choose
 *  to ignore these hints.
 * 
 *  Some methods in this interface use class names.  Such class
 *  names must be given in the Java Virtual Machine internal form of
 *  fully qualified class and interface names.  For convenience '.'
 *  and '/' are interchangeable.  The internal form is defined in
 *  chapter four of
 *  The Java™ Virtual Machine Specification.
 * 
 *  
 *    Discussion: this means that the names
 *    "java/lang.package-info", "java/lang/package-info",
 *    "java.lang.package-info", are valid and equivalent.  Compare to
 *    binary name as defined in
 *    The Java™ Language Specification,
 *    section 13.1 "The Form of a Binary".
 *  
 * 
 *  The case of names is significant.  All names should be treated
 *  as case-sensitive.  For example, some file systems have
 *  case-insensitive, case-aware file names.  File objects representing
 *  such files should take care to preserve case by using File.getCanonicalFile() or similar means.  If the system is
 *  not case-aware, file objects must use other means to preserve case.
 * 
 *  Relative names: some
 *  methods in this interface use relative names.  A relative name is a
 *  non-null, non-empty sequence of path segments separated by '/'.
 *  '.' or '..'  are invalid path segments.  A valid relative name must
 *  match the "path-rootless" rule of RFC 3986,
 *  section 3.3.  Informally, this should be true:
 * 
 *  <!--  URI.create(relativeName).normalize().getPath().equals(relativeName)  -->
 *    URI.create(relativeName).normalize().getPath().equals(relativeName)
 * 
 *  All methods in this interface might throw a SecurityException.
 * 
 *  An object of this interface is not required to support
 *  multi-threaded access, that is, be synchronized.  However, it must
 *  support concurrent access to different file objects created by this
 *  object.
 * 
 *  Implementation note: a consequence of this requirement
 *  is that a trivial implementation of output to a JarOutputStream is not a sufficient implementation.
 *  That is, rather than creating a JavaFileObject that returns the
 *  JarOutputStream directly, the contents must be cached until closed
 *  and then written to the JarOutputStream.
 * 
 *  Unless explicitly allowed, all methods in this interface might
 *  throw a NullPointerException if given a null argument.
 */
trait JavaFileManager extends Closeable with Flushable with OptionChecker {

    /** Releases any resources opened by this file manager directly or
     *  indirectly.
     */
    @stub
    def close(): Unit = ???

    /** Flushes any resources opened for output by this file manager
     *  directly or indirectly.
     */
    @stub
    def flush(): Unit = ???

    /** Gets a class loader for loading plug-ins from the given
     *  location.
     */
    @stub
    def getClassLoader(location: JavaFileManager.Location): ClassLoader = ???

    /** Gets a file object for input
     *  representing the specified relative
     *  name in the specified package in the given location.
     */
    @stub
    def getFileForInput(location: JavaFileManager.Location, packageName: String, relativeName: String): FileObject = ???

    /** Gets a file object for output
     *  representing the specified relative
     *  name in the specified package in the given location.
     */
    @stub
    def getFileForOutput(location: JavaFileManager.Location, packageName: String, relativeName: String, sibling: FileObject): FileObject = ???

    /** Gets a file object for input
     *  representing the specified class of the specified kind in the
     *  given location.
     */
    @stub
    def getJavaFileForInput(location: JavaFileManager.Location, className: String, kind: JavaFileObject.Kind): JavaFileObject = ???

    /** Gets a file object for output
     *  representing the specified class of the specified kind in the
     *  given location.
     */
    @stub
    def getJavaFileForOutput(location: JavaFileManager.Location, className: String, kind: JavaFileObject.Kind, sibling: FileObject): JavaFileObject = ???

    /** Handles one option. */
    @stub
    def handleOption(current: String, remaining: Iterator[String]): Boolean = ???

    /** Determines if a location is known to this file manager. */
    @stub
    def hasLocation(location: JavaFileManager.Location): Boolean = ???

    /** Infers a binary name of a file object based on a location. */
    @stub
    def inferBinaryName(location: JavaFileManager.Location, file: JavaFileObject): String = ???

    /** Compares two file objects and return true if they represent the
     *  same underlying object.
     */
    @stub
    def isSameFile(a: FileObject, b: FileObject): Boolean = ???

    /** Lists all file objects matching the given criteria in the given
     *  location.
     */
    @stub
    def list(location: JavaFileManager.Location, packageName: String, kinds: Set[JavaFileObject.Kind], recurse: Boolean): Iterable[JavaFileObject] = ???
}

object JavaFileManager {
    /** Interface for locations of file objects.  Used by file managers
     *  to determine where to place or search for file objects.
     */
    trait Location {

        /** Gets the name of this location. */
        @stub
        def getName(): String = ???

        /** Determines if this is an output location. */
        @stub
        def isOutputLocation(): Boolean = ???
    }

}
