package javax.tools

import java.lang.{Enum, Object, String}
import scala.scalanative.annotation.stub

/** Standard locations of file objects. */
class StandardLocation private (name: String, ordinal: Int) extends Enum[StandardLocation](name, ordinal) with JavaFileManager.Location {
    
    override def getName(): String = name()
    
    @stub
    override def isOutputLocation(): Boolean = ???
}

object StandardLocation {
    /** Location to search for annotation processors. */
    final val ANNOTATION_PROCESSOR_PATH: StandardLocation = new StandardLocation("ANNOTATION_PROCESSOR_PATH", 0)

    /** Location of new class files. */
    final val CLASS_OUTPUT: StandardLocation = new StandardLocation("CLASS_OUTPUT", 1)

    /** Location to search for user class files. */
    final val CLASS_PATH: StandardLocation = new StandardLocation("CLASS_PATH", 2)

    /** Location of new native header files. */
    final val NATIVE_HEADER_OUTPUT: StandardLocation = new StandardLocation("NATIVE_HEADER_OUTPUT", 3)

    /** Location to search for platform classes. */
    final val PLATFORM_CLASS_PATH: StandardLocation = new StandardLocation("PLATFORM_CLASS_PATH", 4)

    /** Location of new source files. */
    final val SOURCE_OUTPUT: StandardLocation = new StandardLocation("SOURCE_OUTPUT", 5)

    /** Location to search for existing source files. */
    final val SOURCE_PATH: StandardLocation = new StandardLocation("SOURCE_PATH", 6)

    /** Gets the name of this location. */
    @stub
    def getName(): String = ???

    /** Determines if this is an output location. */
    @stub
    def isOutputLocation(): Boolean = ???

    /** Gets a location object with the given name. */
    @stub
    def locationFor(name: String): JavaFileManager.Location = ???

    /** Returns the enum constant of this type with the specified name. */
    @stub
    def valueOf(name: String): StandardLocation = ???

    /** Returns an array containing the constants of this enum type, in
     * the order they are declared.
     */
    @stub
    def values(): Array[StandardLocation] = ???
}
