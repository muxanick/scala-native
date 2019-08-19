package javax.tools

import java.io.{InputStream, OutputStream}
import java.lang.String
import java.util.Set
import javax.lang.model.SourceVersion
import scala.scalanative.annotation.stub

/** Common interface for tools that can be invoked from a program.
 *  A tool is traditionally a command line program such as a compiler.
 *  The set of tools available with a platform is defined by the
 *  vendor.
 * 
 *  Tools can be located using ServiceLoader.load(Class).
 */
trait Tool {

    /** Gets the source versions of the Java™ programming language
     *  supported by this tool.
     */
    @stub
    def getSourceVersions(): Set[SourceVersion] = ???

    /** Run the tool with the given I/O channels and arguments. */
    @stub
    def run(in: InputStream, out: OutputStream, err: OutputStream, arguments: String*): Int = ???
}
