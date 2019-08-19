package java.io

import scalanative.annotation.stub
import scalanative.unsigned._
import scalanative.unsafe._
import scalanative.libc._, stdlib._, stdio._, string._
import scalanative.runtime

class FileInputStream(fd: FileDescriptor, file: Option[File])
    extends InputStream {

  def this(fd: FileDescriptor) = this(fd, None)
  def this(file: File) = this(FileDescriptor.openReadOnly(file), Some(file))
  def this(str: String) = this(new File(str))

  override def available(): Int = {
    fd.available()
  }

  override def close(): Unit =
    fd.close()

  override protected def finalize(): Unit =
    close()

  final def getFD(): FileDescriptor =
    fd

  override def read(): Int = {
    val buffer = new Array[Byte](1)
    if (read(buffer) <= 0) -1
    else buffer(0).toUInt.toInt
  }

  override def read(buffer: Array[Byte]): Int = {
    if (buffer == null) {
      throw new NullPointerException
    }
    read(buffer, 0, buffer.length)
  }

  override def read(buffer: Array[Byte], offset: Int, count: Int): Int = {
    if (buffer == null) {
      throw new NullPointerException
    }
    if (offset < 0 || count < 0 || count > buffer.length - offset) {
      throw new IndexOutOfBoundsException
    }
    if (count == 0) {
      return 0
    }

    // we use the runtime knowledge of the array layout to avoid
    // intermediate buffer, and write straight into the array memory
    val readCount = fd.read(buffer, offset, count)

    if (readCount == 0) {
      // end of file
      -1
    } else if (readCount < 0) {
      // negative value (typically -1) indicates that read failed
      throw new IOException(file.fold("")(_.toString)) //, errno.errno)
    } else {
      // successfully read readCount bytes
      readCount
    }
  }

  override def skip(n: Long): Long =
    if (n < 0) {
      throw new IOException()
    } else {
      val bytesToSkip = Math.min(n, available())
      fd.ignore(bytesToSkip)
      bytesToSkip
    }

  def getChannel: java.nio.channels.FileChannel = new FileChannelInput(this)
}

private class FileChannelInput(val owner: FileInputStream) extends java.nio.channels.FileChannel {
    /** Reads a sequence of bytes from this channel into the given buffer. */
    override def read(dst: java.nio.ByteBuffer): Int = {
        val numBytes = owner.available()
        if (numBytes == 0)
          return -1
        val bytes = new Array[Byte](numBytes)
        owner.read(bytes)
        dst.put(bytes)
        bytes.length
    }

    // Members declared in java.nio.channels.spi.AbstractInterruptibleChannel
    protected override def implCloseChannel(): Unit = {
      owner.close()
    }
    // Members declared in java.nio.channels.FileChannel
    @stub
    override def map(mode: java.nio.channels.FileChannel.MapMode,position: Long,size: Long): java.nio.MappedByteBuffer = ???
    @stub
    override def position(offset: Long): java.nio.channels.FileChannel = ???
    @stub
    override def position(): Long = ???
    @stub
    override def read(buffers: Array[java.nio.ByteBuffer],start: Int,number: Int): Long = ???
    @stub
    override def read(buffer: java.nio.ByteBuffer,position: Long): Int = ???
    @stub
    override def size(): Long = ???
    @stub
    override def transferFrom(src: java.nio.channels.ReadableByteChannel,position: Long,count: Long): Long = ???
    @stub
    override def transferTo(position: Long,count: Long,target: java.nio.channels.WritableByteChannel): Long = ???
    @stub
    override def truncate(size: Long): java.nio.channels.FileChannel = ???
    @stub
    override def write(buffers: Array[java.nio.ByteBuffer],offset: Int,length: Int): Long = ???
    @stub
    override def write(buffer: java.nio.ByteBuffer,position: Long): Int = ???
    @stub
    override def write(src: java.nio.ByteBuffer): Int = ???
}
