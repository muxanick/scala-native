package scala.scalanative
package nir

import java.util.concurrent.atomic.AtomicInteger
import java.nio.ByteBuffer
import scala.scalanative.nir.DebugInfo.IdRef
import serialization.{Tags => T}
import scala.collection.mutable
import scala.language.implicitConversions

sealed abstract class DebugInfo() {
  final def show: String = nir.Show(this)
  def id: Int            = ???

  def lineId(): String = s" !dbg !${id}"

  def isDistinct: Nothing          = ???
  def getLine: String              = ???
  def >>(buffer: ByteBuffer): Unit = ???
  def <<(buffer: ByteBuffer): Unit = ???
  def unref[T <: DebugInfo](implicit dicu: DebugInfoDataBase): T =
    dicu.db.getOrElse(id, null).asInstanceOf[T]
}

sealed class DebugInfoDataBase(n: Int) {
  private val id         = new AtomicInteger(n)
  private val idToDbgInf = mutable.Map.empty[Int, DebugInfo]
  def index: Int         = id.get
  def getNext: Int       = id.getAndIncrement
  def db                 = idToDbgInf
  def add(di: DebugInfo): IdRef = di match {
    case d: IdRef => d
    case _ => {
      if (!db.contains(di.id)) { db.update(di.id, di) }
      new IdRef(di)
    }
  }
  def reset(n: Int): Unit = {
    id.set(n)
    idToDbgInf.clear
  }
}

object DebugInfo {

  val version: Int  = 0
  val revision: Int = 1

  def getOrCreateCompileUnit(file: DebugInfo)(
      implicit dicu: DebugInfoDataBase): IdRef = {
    val di = dicu.db.values
      .find {
        case d: DebugInfo.CompileUnit => file.id == d.id
        case _                        => false
      }
      .getOrElse(DebugInfo.CompileUnit(
        dicu,
        "scala",
        new IdRef(file),
        "scala-native-0.3.0-SNAPSHOT",
        isOptimized = true,
        runtimeVersion = 0,
        emissionKind = "FullDebug",
        enums = new IdRef(DebugInfo.getOrCreateEnums),
        globals = new IdRef(DebugInfo.getOrCreateGlobals),
        dicu.getNext
      ))
    dicu.add(di)
  }

  def getOrCreateFile(filename: String)(
      implicit dicu: DebugInfoDataBase): IdRef = {
    val di = dicu.db.values
      .find {
        case DebugInfo.File(f, _, _) => filename == f
        case _                       => false
      }
      .getOrElse(DebugInfo.File(filename, "", dicu.getNext))
    dicu.add(di)
  }

  def getOrCreateEnums()(implicit dicu: DebugInfoDataBase): IdRef = {
    dicu.add(new DebugInfo.Info(Seq.empty, dicu.getNext))
  }

  def getOrCreateGlobals()(implicit dicu: DebugInfoDataBase): IdRef = {
    dicu.add(new DebugInfo.Info(Seq.empty, dicu.getNext))
  }

  def getOrCreateSubprogram(name: String, line: Int)(
      implicit dicu: DebugInfoDataBase): IdRef = {
    val di = dicu.db.values
      .find {
        case DebugInfo.Subprogram(n, _, _, l, _, _, _, _, _, _, _, _, _) =>
          name == n && l == line
        case _ => false
      }
      .getOrElse(
        new DebugInfo.Subprogram(name,
                                 IdRef(-1),
                                 IdRef(-1),
                                 line,
                                 IdRef(-1),
                                 isLocal = true,
                                 isDefinition = true,
                                 -1,
                                 "",
                                 isOptimized = true,
                                 IdRef(-1),
                                 IdRef(-1),
                                 dicu.getNext))
    dicu.add(di)
  }

  val None = IdRef(-1)

  def <<(buffer: ByteBuffer)(
      implicit dicu: DebugInfoDataBase): DebugInfo.IdRef = {
    val id = buffer.getInt
    dicu.add(buffer.getInt match {
      case T.IdRefDbgInf       => new DebugInfo.IdRef(id, buffer)
      case T.CompileUnitDbgInf => new DebugInfo.CompileUnit(id, buffer)
      case T.FileDbgInf        => new DebugInfo.File(id, buffer)
      case T.InfoDbgInf        => new DebugInfo.Info(id, buffer)
      case T.SubprogramDbgInf  => new DebugInfo.Subprogram(id, buffer)
      case T.LocationDbgInf    => new DebugInfo.Location(id, buffer)
    })
  }

  def <<(name: Global): DebugInfo = {
    None
  }

  private implicit def intToUtilityInt(value: Int): UtilityInt =
    new UtilityInt(value)
  private implicit def stringToUtilityString(value: String): UtilityString =
    new UtilityString(value)
  private implicit def booleanToUtilityBoolean(
      value: Boolean): UtilityBoolean = new UtilityBoolean(value)

  final case class IdRef(override val id: Int) extends DebugInfo {
    def this(newid: Int, buffer: ByteBuffer) = {
      this(newid)
    }
    def this(ref: DebugInfo) = {
      this(ref.id)
    }

    override def getLine(): String = {
      null //dicu.db.getOrElse(id, null).getLine
    }

    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.IdRefDbgInf >> buffer
    }
  }

  final case class GlobalVariableExpression(variable: IdRef,
                                            override val id: Int)
      extends DebugInfo {
    def this(newid: Int, buffer: ByteBuffer)(
        implicit dicu: DebugInfoDataBase) = {
      this(DebugInfo << buffer, newid)
    }

    override def getLine(): String = {
      s"!${id} = !DIGlobalVariableExpression(var: !${variable.id})"
    }

    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.IdRefDbgInf >> buffer
    }
  }

  final case class CompileUnit(dicu: DebugInfoDataBase,
                               language: String,
                               file: IdRef,
                               producer: String,
                               isOptimized: Boolean,
                               runtimeVersion: Int,
                               emissionKind: String,
                               enums: IdRef,
                               globals: IdRef,
                               override val id: Int)
      extends DebugInfo {
    def this(newid: Int, buffer: ByteBuffer)(
        implicit dicu: DebugInfoDataBase) = {
      this(
        dicu,
        getString(buffer),
        DebugInfo << buffer,
        getString(buffer),
        getBoolean(buffer),
        buffer.getInt,
        getString(buffer),
        DebugInfo << buffer,
        DebugInfo << buffer,
        newid
      )
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
  }

  final case class File(filename: String,
                        directory: String,
                        override val id: Int)
      extends DebugInfo {
    def this(newid: Int, buffer: ByteBuffer)(
        implicit dicu: DebugInfoDataBase) = {
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
  }

  final case class Info(args: Seq[Any], override val id: Int)
      extends DebugInfo {
    def this(newid: Int, buffer: ByteBuffer)(
        implicit dicu: DebugInfoDataBase) = {
      this(Seq.empty, newid)
    }
    override def getLine(): String = {
      args.mkString(s"!${id} = !{", ", ", "}")
    }

    override def >>(buffer: ByteBuffer): Unit = {
      id >> buffer
      T.InfoDbgInf >> buffer
    }
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
                              override val id: Int)
      extends DebugInfo {
    def this(newid: Int, buffer: ByteBuffer)(
        implicit dicu: DebugInfoDataBase) = {
      this(
        getString(buffer),
        DebugInfo << buffer,
        DebugInfo << buffer,
        buffer.getInt,
        DebugInfo << buffer,
        getBoolean(buffer),
        getBoolean(buffer),
        buffer.getInt,
        getString(buffer),
        getBoolean(buffer),
        DebugInfo << buffer,
        DebugInfo << buffer,
        newid
      )
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
  }

  final case class Location(line: Int,
                            column: Int,
                            scope: IdRef,
                            override val id: Int)
      extends DebugInfo {
    def this(newid: Int, buffer: ByteBuffer)(
        implicit dicu: DebugInfoDataBase) = {
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
  }

  private val pool = new scala.scalanative.io.ByteBufferPool

  def serialize(buffer: ByteBuffer)(implicit dicu: DebugInfoDataBase): Unit = {
    val local = pool.claim()
    local.clear
    version >> local
    revision >> local
    dicu.index >> local
    dicu.db.size >> local
    dicu.db.foreach(x => {
      x._1 >> local
      x._2 >> local
    })

    local.position() >> buffer
    local.flip
    buffer.put(local)
    pool.reclaim(local)
  }

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
  private def getBoolean(implicit buffer: ByteBuffer): Boolean =
    buffer.get != 0
  private def putString(v: String)(implicit buffer: ByteBuffer) = {
    val bytes = v.getBytes("UTF-8")
    buffer.putInt(bytes.length); buffer.put(bytes)
  }
  private def putBoolean(v: Boolean)(implicit buffer: ByteBuffer) =
    buffer.put((if (v) 1 else 0).toByte)
  private class UtilityInt(val value: Int) {
    def >>(buf: ByteBuffer): Unit = {
      buf.putInt(value)
    }
  }
  private class UtilityBoolean(val value: Boolean) {
    def >>(buf: ByteBuffer): Unit = {
      putBoolean(value)(buf)
    }
  }
  private class UtilityString(val value: String) {
    def >>(buf: ByteBuffer): Unit = {
      putString(value)(buf)
    }
  }
}
