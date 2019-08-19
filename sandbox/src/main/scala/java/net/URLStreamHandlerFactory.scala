package java.net

import java.lang.String
import scala.scalanative.annotation.stub

/** This interface defines a factory for URL stream
 *  protocol handlers.
 *  
 *  It is used by the URL class to create a
 *  URLStreamHandler for a specific protocol.
 */
trait URLStreamHandlerFactory {

    /** Creates a new URLStreamHandler instance with the specified
     *  protocol.
     */
    @stub
    def createURLStreamHandler(protocol: String): URLStreamHandler = ???
}
