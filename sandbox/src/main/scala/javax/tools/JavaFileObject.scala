package javax.tools

import java.lang.{Enum, Object, String}
import javax.lang.model.element.{Modifier, NestingKind}
import scala.scalanative.annotation.stub

/** File abstraction for tools operating on Java™ programming language
 *  source and class files.
 * 
 *  All methods in this interface might throw a SecurityException if
 *  a security exception occurs.
 * 
 *  Unless explicitly allowed, all methods in this interface might
 *  throw a NullPointerException if given a null argument.
 */
trait JavaFileObject extends FileObject {

    /** Provides a hint about the access level of the class represented
     *  by this file object.
     */
    @stub
    def getAccessLevel(): Modifier = ???

    /** Gets the kind of this file object. */
    @stub
    def getKind(): JavaFileObject.Kind = ???

    /** Provides a hint about the nesting level of the class
     *  represented by this file object.
     */
    @stub
    def getNestingKind(): NestingKind = ???

    /** Checks if this file object is compatible with the specified
     *  simple name and kind.
     */
    @stub
    def isNameCompatible(simpleName: String, kind: JavaFileObject.Kind): Boolean = ???
}

object JavaFileObject {
    /** Kinds of JavaFileObjects. */

    class Kind private (name: String, ordinal: Int) extends Enum[Kind](name, ordinal)

    object Kind {
        /** Class files for the Java Virtual Machine. */
        final val CLASS: Kind = new Kind("CLASS", 0)

        /** HTML files. */
        final val HTML: Kind = new Kind("HTML", 1)

        /** Any other kind. */
        final val OTHER: Kind = new Kind("OTHER", 2)

        /** Source files written in the Java programming language. */
        final val SOURCE: Kind = new Kind("SOURCE", 3)

        /** The extension which (by convention) is normally used for
        *  this kind of file object.
        */
        @stub
        val extension: String = ???

        /** Returns the enum constant of this type with the specified name. */
        @stub
        def valueOf(name: String): Kind = ???

        /** Returns an array containing the constants of this enum type, in
        * the order they are declared.
        */
        @stub
        def values(): Array[Kind] = ???
    }
}
