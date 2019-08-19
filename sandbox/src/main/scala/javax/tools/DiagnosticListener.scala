package javax.tools

import scala.scalanative.annotation.stub

/** Interface for receiving diagnostics from tools. */
trait DiagnosticListener[S] {

    /** Invoked when a problem is found. */
    @stub
    def report(diagnostic: Diagnostic[_ <: S]): Unit = ???
}
