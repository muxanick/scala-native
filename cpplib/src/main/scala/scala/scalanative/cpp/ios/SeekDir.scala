package scala.scalanative.cpp.ios
import scalanative.unsafe._

object SeekDir {
    final val beg: Bitmask = SeekDirNative.beg
    final val end: Bitmask = SeekDirNative.end
    final val cur: Bitmask = SeekDirNative.cur
}

@extern
private[cpp] object SeekDirNative {
  @name("scalanative_cpp_ios_seekdir_beg")
  def beg: Bitmask = extern

  @name("scalanative_cpp_ios_seekdir_end")
  def end: Bitmask = extern

  @name("scalanative_cpp_ios_seekdir_cur")
  def cur: Bitmask = extern
}