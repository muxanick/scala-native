package scala.scalanative.cpp.ios

import scalanative.unsafe._
import scala.scalanative.cpp._

class IOStream(obj: NativeObject) extends CppObject(obj) {
  def this() = this(NullObj)

  override def close(): Unit = {
  }

  def sync(): Unit = IOStreamNative.sync(getNativeObject())

  def valid(): Boolean = (obj != NullObj)

  def streambuf_in_avail(): ios.Streamsize = IOStreamNative.streambuf_in_avail(getNativeObject())

  def ignore(count: Streamsize): Unit = IOStreamNative.ignore(getNativeObject(), count)

  def read(buf: Ptr[_], count: Streamsize): Streamsize = {
    IOStreamNative.read(getNativeObject(), buf, count)
    count
  }

  def read(): CChar = {
    var buf = stackalloc[CChar]
    read(buf, 1)
    buf(0)
  }

  def write(buf: Ptr[_], count: Streamsize): Streamsize = {
    IOStreamNative.write(getNativeObject(), buf, count)
    count
  }

  def seekg(offset: Streamsize): Unit = 
    IOStreamNative.seekg(getNativeObject(), offset, SeekDir.beg)

  def seekg(offset: Streamsize, dir: Bitmask): Unit = 
    IOStreamNative.seekg(getNativeObject(), offset, dir)

  def tellg(): Streamsize = IOStreamNative.tellg(getNativeObject())
}

object IOStream {
  val stdin: IOStream  = new IOStream(IOStreamNative.stdin)
  val stdout: IOStream = new IOStream(IOStreamNative.stdout)
  val stderr: IOStream = new IOStream(IOStreamNative.stderr)
}

@extern
private[cpp] object IOStreamNative {

  @name("scalanative_cpp_ios_stdin")
  def stdin(): NativeObject = extern

  @name("scalanative_cpp_ios_stdout")
  def stdout(): NativeObject = extern

  @name("scalanative_cpp_ios_stderr")
  def stderr(): NativeObject = extern

  @name("scalanative_cpp_ios_iostream_sync")
  def sync(obj: NativeObject): Unit = extern

  @name("scalanative_cpp_ios_iostream_streambuf_in_avail")
  def streambuf_in_avail(obj: NativeObject): Streamsize = extern

  @name("scalanative_cpp_ios_iostream_read")
  def read(obj: NativeObject, buf: Ptr[_], count: Streamsize): Streamsize =
    extern

  @name("scalanative_cpp_ios_iostream_write")
  def write(obj: NativeObject, buf: Ptr[_], count: Streamsize): Streamsize =
    extern

  @name("scalanative_cpp_ios_iostream_ignore")
  def ignore(obj: NativeObject, count: Streamsize): Unit = extern

  @name("scalanative_cpp_ios_iostream_seekg")
  def seekg(obj: NativeObject, offset: Streamsize, dir: Bitmask): Unit = extern
  
  @name("scalanative_cpp_ios_iostream_tellg")
  def tellg(obj: NativeObject): Streamsize = extern
}
