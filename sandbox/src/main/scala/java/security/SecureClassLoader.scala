package java.security

import java.lang.{Class, ClassLoader, Object, String}
import java.nio.ByteBuffer
import scala.scalanative.annotation.stub

/** This class extends ClassLoader with additional support for defining
 *  classes with an associated code source and permissions which are
 *  retrieved by the system policy by default.
 */
class SecureClassLoader(parent: ClassLoader) extends ClassLoader {
    /** Creates a new SecureClassLoader using the specified parent
     *  class loader for delegation.
     */
    
    
    /** Creates a new SecureClassLoader using the default parent class
     *  loader for delegation.
     */
    protected def this() = this(null)

    /** Converts an array of bytes into an instance of class Class,
     *  with an optional CodeSource.
     */
    @stub
    protected def defineClass(name: String, b: Array[Byte], off: Int, len: Int, cs: CodeSource): Class[_] = ???

    /** Converts a ByteBuffer
     *  into an instance of class Class, with an optional CodeSource.
     */
    @stub
    protected def defineClass(name: String, b: ByteBuffer, cs: CodeSource): Class[_] = ???

    /** Returns the permissions for the given CodeSource object. */
    @stub
    protected def getPermissions(codesource: CodeSource): PermissionCollection = ???
}
