package javax.tools

import java.io.{InputStream, OutputStream, Reader, Writer}
import java.lang.{CharSequence, String}
import java.net.URI
import scala.scalanative.annotation.stub

/** File abstraction for tools.  In this context, file means
 *  an abstraction of regular files and other sources of data.  For
 *  example, a file object can be used to represent regular files,
 *  memory cache, or data in databases.
 * 
 *  All methods in this interface might throw a SecurityException if
 *  a security exception occurs.
 * 
 *  Unless explicitly allowed, all methods in this interface might
 *  throw a NullPointerException if given a null argument.
 */
trait FileObject {

    /** Deletes this file object. */
    @stub
    def delete(): Boolean = ???

    /** Gets the character content of this file object, if available. */
    @stub
    def getCharContent(ignoreEncodingErrors: Boolean): CharSequence = ???

    /** Gets the time this file object was last modified. */
    @stub
    def getLastModified(): Long = ???

    /** Gets a user-friendly name for this file object. */
    @stub
    def getName(): String = ???

    /** Gets an InputStream for this file object. */
    @stub
    def openInputStream(): InputStream = ???

    /** Gets an OutputStream for this file object. */
    @stub
    def openOutputStream(): OutputStream = ???

    /** Gets a reader for this object. */
    @stub
    def openReader(ignoreEncodingErrors: Boolean): Reader = ???

    /** Gets a Writer for this file object. */
    @stub
    def openWriter(): Writer = ???

    /** Returns a URI identifying this file object. */
    @stub
    def toUri(): URI = ???
}
