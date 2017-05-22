package scala.scalanative
package nir

import java.util.concurrent.atomic.AtomicInteger
import java.nio.ByteBuffer
import serialization.{Tags => T}
import scala.collection.mutable
import scala.language.implicitConversions

sealed abstract class DebugInfo() {  
  final def show: String = nir.Show(this)
  def id:Int = ???

  def lineId(): String = s" !dbg !${id}"

  def isDistinct = ???
  def getLine(): String = ???
  def >>(buffer: ByteBuffer): Unit = ???
  def <<(buffer: ByteBuffer): Unit = ???
}

final object DebugInfo {

  // clear all data
  def reset(): Unit = {
   id = new AtomicInteger(0)
   idToDbgInf.clear
  }

  def getOrCreateCompileUnit(file: DebugInfo): IdRef = {
    val di = idToDbgInf.values.find( _ match {
      case DebugInfo.CompileUnit(_, f, _, opt, _, _, _, _, _) => file.id == f.id
      case _ => false
    }).getOrElse( DebugInfo.CompileUnit("scala",
                                            new IdRef(file),
                                            "scala-native-0.3.0-SNAPSHOT",
                                            isOptimized = true,
                                            runtimeVersion = 0,
                                            emissionKind = "FullDebug",
                                            enums = new IdRef(DebugInfo.getOrCreateEnums()),
                                            globals = new IdRef(DebugInfo.getOrCreateGlobals()))
       )
       new IdRef(di)
  }

  def getOrCreateFile(filename: String): IdRef = {
    val di = idToDbgInf.values.find( _ match {
      case DebugInfo.File(f, _, _) => filename == f
      case _ => false
    }).getOrElse ( DebugInfo.File(filename, "") )
    new IdRef(di)
  }

  def getOrCreateEnums(): IdRef = {
    new IdRef( new DebugInfo.Info(Seq.empty))
  }

  def getOrCreateGlobals(): IdRef = {
    new IdRef( new DebugInfo.Info(Seq.empty))
  }

  def getOrCreateSubprogram(name:String, line:Int): IdRef = {
    val di = idToDbgInf.values.find( _ match {
      case DebugInfo.Subprogram(n, _, _, l, _, _, _, _, _, _, _, _, _) => (name == n && l == line)
      case _ => false
    }).getOrElse ( DebugInfo.Subprogram(name,
                                        IdRef(-1),
                                        IdRef(-1),
                                        line,
                                        IdRef(-1),
                                        true,
                                        true,
                                        -1,
                                        "",
                                        true,
                                        IdRef(-1),
                                        IdRef(-1)                                        
                                        ) )
    new IdRef(di)
  }
  
  def <<(buffer: ByteBuffer): DebugInfo.IdRef = {
    val id = buffer.getInt
    buffer.getInt match {
      case T.IdRefDbgInf => new DebugInfo.IdRef(id, buffer)
      case T.CompileUnitDbgInf => new DebugInfo.IdRef(new DebugInfo.CompileUnit(id, buffer))
      case T.FileDbgInf => new DebugInfo.IdRef(new DebugInfo.File(id, buffer))
      case T.InfoDbgInf => new DebugInfo.IdRef(new DebugInfo.Info(id, buffer))
      case T.SubprogramDbgInf => new DebugInfo.IdRef(new DebugInfo.Subprogram(id, buffer))
      case T.LocationDbgInf => new DebugInfo.IdRef(new DebugInfo.Location(id, buffer))
    }
  }

  def <<(name: Global): DebugInfo = name match {
    case Global.Top(id)       => new DebugInfo.CompileUnit("scala", null, "scala-native 0.3.0-SNAPSHOT", true, 0, "LineTablesOnly", null, null)
    case Global.Member(n, id) => new DebugInfo.Subprogram(s"$id", null, null, 1, null, true, true, 88, "DIFlagPrototyped", true, new DebugInfo.IdRef(0), null)
    case _ => null
  }

  def getNext(): Int = id.getAndIncrement()
  private implicit def intToUtilityInt(value: Int) = new UtilityInt(value)
  private implicit def stringToUtilityString(value: String) = new UtilityString(value)
  private implicit def booleanToUtilityBoolean(value: Boolean) = new UtilityBoolean(value)

  final case class IdRef(override val id:Int = getNext) extends DebugInfo {
    def this(newid:Int, buffer: ByteBuffer) = {
      this(newid)
    }
    def this(ref:DebugInfo) = {
      this(ref.id)
    }
    override def getLine(): String = {
      idToDbgInf.getOrElse(id, null).getLine
    }

    override def >>(buffer: ByteBuffer): Unit = {
      import buffer._
      id >> buffer
      T.IdRefDbgInf >> buffer
    }
  }

  final case class GlobalVariableExpression(variable: IdRef, override val id:Int = getNext) extends DebugInfo {
    def this(newid:Int, buffer: ByteBuffer) = {
      this(DebugInfo << buffer, newid)
    }

    override def getLine(): String = {
      s"!${id} = !DIGlobalVariableExpression(var: !${variable.id})"
    }

    override def >>(buffer: ByteBuffer): Unit = {
      import buffer._
      id >> buffer
      T.IdRefDbgInf >> buffer
    }
  }

  
  
  final case class CompileUnit(language: String,
                               file: IdRef,
                               producer: String,
                               isOptimized: Boolean,
                               runtimeVersion: Int,
                               emissionKind: String,
                               enums: IdRef,
                               globals: IdRef,
                               override val id:Int = getNext) extends DebugInfo {
    def this(newid:Int, buffer: ByteBuffer) = {
      this(getString(buffer), DebugInfo << buffer, getString(buffer), getBoolean(buffer), buffer.getInt, getString(buffer), DebugInfo << buffer, DebugInfo << buffer, newid)
    }
    override def getLine(): String = {
       s"!${id} = !DICompileUnit(language: ${language}, file: !${file.id}, producer: ${producer}, isOptimized: ${isOptimized}, runtimeVersion: ${runtimeVersion}, emissionKind: ${emissionKind}, enums: !${enums.id}, globals: !${globals.id})"
    }

    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.CompileUnitDbgInf >> buffer
      language >> buffer
      file >> buffer
      producer >> buffer
      isOptimized >> buffer
      runtimeVersion >> buffer
      emissionKind >> buffer
      enums >> buffer
      globals >> buffer
    }

    idToDbgInf.update(id, this)
  }

  final case class File(filename: String, directory: String, override val id:Int = getNext) extends DebugInfo {
    def this(newid:Int, buffer: ByteBuffer) = {
      this(getString(buffer), getString(buffer), newid)
    }
    override def getLine(): String = {
        s"!${id} = !DIFile(filename: ${escapeString(filename)}, directory: ${escapeString(directory)})"
    }

    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.FileDbgInf >> buffer
      filename >> buffer
      directory >> buffer
    }

    idToDbgInf.update(id, this)
  }

  final case class Info(args: Seq[Any], override val id:Int = getNext) extends DebugInfo {
    def this(newid:Int, buffer: ByteBuffer) = {
      this(Seq.empty, newid)
    }
    override def getLine(): String = {
      args.mkString(s"!${id} = !{", ", ", "}")
    }

    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.InfoDbgInf >> buffer
    }

    idToDbgInf.update(id, this)
  }

  final case class Subprogram(name: String,
                              scope: IdRef,
                              file: IdRef,
                              line: Int,
                              rtype: IdRef,
                              isLocal: Boolean,
                              isDefinition: Boolean,
                              scopeLine: Int,
                              flags: String,
                              isOptimized: Boolean,
                              unit: IdRef,
                              variables: IdRef,
                              override val id:Int = getNext) extends DebugInfo {
    def this(newid:Int, buffer: ByteBuffer) = {
      this(getString(buffer), DebugInfo << buffer, DebugInfo << buffer, buffer.getInt, DebugInfo << buffer, getBoolean(buffer), getBoolean(buffer), buffer.getInt, getString(buffer), getBoolean(buffer), DebugInfo << buffer, DebugInfo << buffer, newid)
    }
    override def getLine(): String = {
      s"!${id} = !DISubprogram(name: ${name}, scope: !${scope.id}, file: !${file.id}, line: ${line}, type: !${rtype.id}, isLocal: ${isLocal}, isDefinition: ${isDefinition}, scopeLine: ${scopeLine}, flags: ${flags}, isOptimized: ${isOptimized}, unit: !${unit.id}, variables: !${variables.id})"
    }
    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.SubprogramDbgInf >> buffer
      name >> buffer
      scope >> buffer
      file >> buffer
      line >> buffer
      rtype >> buffer
      isLocal >> buffer
      isDefinition >> buffer
      scopeLine >> buffer
      flags >> buffer
      isOptimized >> buffer
      unit >> buffer
      variables >> buffer
    }

    idToDbgInf.update(id, this)
  }
  
  final case class Location(line: Int, column: Int, scope: IdRef, override val id:Int = getNext) extends DebugInfo {
    def this(newid:Int, buffer: ByteBuffer) = {
      this(buffer.getInt, buffer.getInt, DebugInfo << buffer, newid)
    }
    override def getLine(): String = {
      s"!${id} = !DILocation(line: ${line}, column: ${column}, scope: !${scope.id})"
    }

    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.LocationDbgInf >> buffer
      line >> buffer
      column >> buffer
      scope >> buffer
    }

    idToDbgInf.update(id, this)
  }

  private var id = new AtomicInteger(0)
  private val idToDbgInf = mutable.Map.empty[Int, DebugInfo]

  def escapeString(s: String): String = {
    s.replace("\\", "\\5C")
  }

  // todo: move them to utility object
  // utility methods
  private def getString(implicit buffer: ByteBuffer): String = {
    val arr = new Array[Byte](buffer.getInt)
    buffer.get(arr)
    new String(arr, "UTF-8")
  }
  private def getBoolean(implicit buffer: ByteBuffer): Boolean = buffer.get != 0
  private def putString(v: String)(implicit buffer: ByteBuffer) = {
    val bytes = v.getBytes("UTF-8")
    buffer.putInt(bytes.length); buffer.put(bytes)
  }
  private def putBoolean(v: Boolean)(implicit buffer: ByteBuffer) = buffer.put((if (v) 1 else 0).toByte)
  private class UtilityInt(val value: Int) {
    def >>(buf: ByteBuffer):Unit = {
      buf.putInt(value)
    }
  }
  private class UtilityBoolean(val value: Boolean) {
    def >>(buf: ByteBuffer):Unit = {
      putBoolean(value)(buf)
    }
  }
  private class UtilityString(val value: String) {
    def >>(buf: ByteBuffer):Unit = {
      putString(value)(buf)
    }
  }
}
