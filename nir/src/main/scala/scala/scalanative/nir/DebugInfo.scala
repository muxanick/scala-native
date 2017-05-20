package scala.scalanative
package nir

import java.util.concurrent.atomic.AtomicInteger

sealed abstract class DebugInfo(val id:Int) {
  final def show: String = nir.Show(this)

  def isDistinct = ???
  
  def getLine(): String = this match {
    case DebugInfo.CompileUnit(_, _, _, _, _, _, _)               =>
    case DebugInfo.Info(_)                                        =>
    case DebugInfo.Location(line, column, scope)                  => s"!${id} = !DILocation(line: ${line}, column: ${directory}, scope: !${scope.id})"
    case DebugInfo.Subprogram(_, _, _, _, _, _, _, _, _, _, _, _) => 
    case DebugInfo.File(filename, directory)                      => s"!${id} = !DIFile(filename: ${escapeString(filename)}, directory: ${escapeString(directory)})"
  }

  def escapeString(s: String): String = {
    s
  }

}

object DebugInfo {
  private var id = new AtomicInteger(0)
  def getNext(): Int = id.getAndIncrement()
  def reset(): Unit = id = new AtomicInteger(0)
  final case class CompileUnit(language: String, file: File, producer: String, isOptimized: Boolean, runtimeVersion: Int, emissionKind: String, enums: DebugInfo) extends DebugInfo(getNext)
  final case class File(filename: String, directory: String) extends DebugInfo(getNext)  
  final case class Info(args: Any*) extends DebugInfo(getNext)
  final case class Subprogram(name: String, scope: DebugInfo, file: File, line: Int, rtype: DebugInfo, isLocal: Boolean, isDefinition: Boolean, scopeLine: Int, flags: String, isOptimized: Boolean, unit: DebugInfo, variables: DebugInfo) extends DebugInfo(getNext)
  final case class Location(line: Int, column: Int, scope: DebugInfo) extends DebugInfo(getNext)
}
