package java.io

import java.lang.{Object, String}
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import scala.scalanative.annotation.stub
import scalanative.runtime
import scalanative.cpp.ios.{FStream, IOStream}
import scalanative.unsafe._
import scala.scalanative.cpp.filesystem.{Filesystem, Path}

/** Instances of this class support both reading and writing to a
 *  random access file. A random access file behaves like a large
 *  array of bytes stored in the file system. There is a kind of cursor,
 *  or index into the implied array, called the file pointer;
 *  input operations read bytes starting at the file pointer and advance
 *  the file pointer past the bytes read. If the random access file is
 *  created in read/write mode, then output operations are also available;
 *  output operations write bytes starting at the file pointer and advance
 *  the file pointer past the bytes written. Output operations that write
 *  past the current end of the implied array cause the array to be
 *  extended. The file pointer can be read by the
 *  getFilePointer method and set by the seek
 *  method.
 *  
 *  It is generally true of all the reading routines in this class that
 *  if end-of-file is reached before the desired number of bytes has been
 *  read, an EOFException (which is a kind of
 *  IOException) is thrown. If any byte cannot be read for
 *  any reason other than end-of-file, an IOException other
 *  than EOFException is thrown. In particular, an
 *  IOException may be thrown if the stream has been closed.
 */
class RandomAccessFile (private[io] val file: File) extends Object with DataOutput with DataInput with Closeable {

    private[io] var fd: FileDescriptor = _
    /** Creates a random access file stream to read from, and optionally to
     *  write to, the file specified by the File argument.
     */
    def this(file: File, mode: String) = {
        this(file)
        if (mode != "r" && mode != "rw" && mode != "rws" && mode != "rwd")
            throw new FileNotFoundException("Cannot open file " + file.getPath)
        this.fd = FileDescriptor.open(file, mode)
    }

    /** Creates a random access file stream to read from, and optionally
     *  to write to, a file with the specified name.
     */
    def this(name: String, mode: String) = this(new File(name), mode)

    /** Closes this random access file stream and releases any system
     *  resources associated with the stream.
     */
    def close(): Unit = fd.close()

    /** Returns the unique FileChannel
     *  object associated with this file.
     */
    def getChannel(): FileChannel = new FileChannelRandomAccess(this)

    /** Returns the opaque file descriptor object associated with this
     *  stream.
     */
    def getFD(): FileDescriptor = fd

    /** Returns the current offset in this file. */
    def getFilePointer(): Long = fd.stream.tellg()

    /** Returns the length of this file. */
    def length(): Long = {
        Filesystem.file_size(file.getPath())
    }

    /** Reads a byte of data from this file. */
    def read(): Int = fd.stream.read()

    /** Reads up to b.length bytes of data from this file
     *  into an array of bytes.
     */
    def read(b: Array[Byte]): Int = read(b, 0, b.length)

    /** Reads up to len bytes of data from this file into an
     *  array of bytes.
     */
    def read(b: Array[Byte], off: Int, len: Int): Int = {
        val buf = b.asInstanceOf[runtime.ByteArray].at(off)
        fd.stream.read(buf, len).toInt
    }

    /** Reads a boolean from this file. */
    @stub
    def readBoolean(): Boolean = ???

    /** Reads a signed eight-bit value from this file. */
    @stub
    def readByte(): Byte = ???

    /** Reads a character from this file. */
    @stub
    def readChar(): Char = ???

    /** Reads a double from this file. */
    @stub
    def readDouble(): Double = ???

    /** Reads a float from this file. */
    @stub
    def readFloat(): Float = ???

    /** Reads b.length bytes from this file into the byte
     *  array, starting at the current file pointer.
     */
    @stub
    def readFully(b: Array[Byte]): Unit = ???

    /** Reads exactly len bytes from this file into the byte
     *  array, starting at the current file pointer.
     */
    @stub
    def readFully(b: Array[Byte], off: Int, len: Int): Unit = ???

    /** Reads a signed 32-bit integer from this file. */
    @stub
    def readInt(): Int = ???

    /** Reads the next line of text from this file. */
    @stub
    def readLine(): String = ???

    /** Reads a signed 64-bit integer from this file. */
    @stub
    def readLong(): Long = ???

    /** Reads a signed 16-bit number from this file. */
    @stub
    def readShort(): Short = ???

    /** Reads an unsigned eight-bit number from this file. */
    @stub
    def readUnsignedByte(): Int = ???

    /** Reads an unsigned 16-bit number from this file. */
    @stub
    def readUnsignedShort(): Int = ???

    /** Reads in a string from this file. */
    @stub
    def readUTF(): String = ???

    /** Sets the file-pointer offset, measured from the beginning of this
     *  file, at which the next read or write occurs.
     */
    def seek(pos: Long): Unit = fd.stream.seekg(pos)

    /** Sets the length of this file. */
    @stub
    def setLength(newLength: Long): Unit = ???

    /** Attempts to skip over n bytes of input discarding the
     *  skipped bytes.
     */
    @stub
    def skipBytes(n: Int): Int = ???

    /** Writes b.length bytes from the specified byte array
     *  to this file, starting at the current file pointer.
     */
    @stub
    def write(b: Array[Byte]): Unit = ???

    /** Writes len bytes from the specified byte array
     *  starting at offset off to this file.
     */
    @stub
    def write(b: Array[Byte], off: Int, len: Int): Unit = ???

    /** Writes the specified byte to this file. */
    @stub
    def write(b: Int): Unit = ???

    /** Writes a boolean to the file as a one-byte value. */
    @stub
    def writeBoolean(v: Boolean): Unit = ???

    /** Writes a byte to the file as a one-byte value. */
    @stub
    def writeByte(v: Int): Unit = ???

    /** Writes the string to the file as a sequence of bytes. */
    @stub
    def writeBytes(s: String): Unit = ???

    /** Writes a char to the file as a two-byte value, high
     *  byte first.
     */
    @stub
    def writeChar(v: Int): Unit = ???

    /** Writes a string to the file as a sequence of characters. */
    @stub
    def writeChars(s: String): Unit = ???

    /** Converts the double argument to a long using the
     *  doubleToLongBits method in class Double,
     *  and then writes that long value to the file as an
     *  eight-byte quantity, high byte first.
     */
    @stub
    def writeDouble(v: Double): Unit = ???

    /** Converts the float argument to an int using the
     *  floatToIntBits method in class Float,
     *  and then writes that int value to the file as a
     *  four-byte quantity, high byte first.
     */
    @stub
    def writeFloat(v: Float): Unit = ???

    /** Writes an int to the file as four bytes, high byte first. */
    @stub
    def writeInt(v: Int): Unit = ???

    /** Writes a long to the file as eight bytes, high byte first. */
    @stub
    def writeLong(v: Long): Unit = ???

    /** Writes a short to the file as two bytes, high byte first. */
    @stub
    def writeShort(v: Int): Unit = ???

    /** Writes a string to the file using
     *  modified UTF-8
     *  encoding in a machine-independent manner.
     */
    @stub
    def writeUTF(str: String): Unit = ???
}

private class FileChannelRandomAccess(val owner: RandomAccessFile) extends FileChannel {
    /** Reads a sequence of bytes from this channel into the given buffer. */
    override def read(dst: ByteBuffer): Int = {
        val numBytes = (owner.length() - owner.getFilePointer()).toInt
        val bytes = new Array[Byte](numBytes)
        owner.read(bytes, 0, numBytes)
        dst.put(bytes)
        bytes.length
    }

    // Members declared in java.nio.channels.spi.AbstractInterruptibleChannel
    @stub
    protected override def implCloseChannel(): Unit = ???
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