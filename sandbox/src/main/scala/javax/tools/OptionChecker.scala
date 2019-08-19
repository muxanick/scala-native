package javax.tools

import java.lang.String
import scala.scalanative.annotation.stub

/** Interface for recognizing options. */
trait OptionChecker {

    /** Determines if the given option is supported and if so, the
     *  number of arguments the option takes.
     */
    @stub
    def isSupportedOption(option: String): Int = ???
}
