package scala.scalanative.cpp

import scala.scalanative.annotation.alwaysinline
import scalanative.unsafe._

import scala.scalanative.cpp._

class CppObject(private var nativeObject: NativeObject) {
  def this() = this(NullObj)

  @alwaysinline def setNativeObject(obj: NativeObject): Unit = {
    nativeObject = obj
  }

  @alwaysinline def getNativeObject(): NativeObject = nativeObject
  
  def close(): Unit = {
    CppObjectNative.destroy_object(nativeObject)
    nativeObject = NullObj
  }

  def autoClose[E <: CppObject](): E = {
    // close when parent has been deleted
    this.asInstanceOf[E]
  }
}

@extern
private[cpp] object CppObjectNative
{
    @name("scalanative_cpp_destroy_object")
    def destroy_object(obj: NativeObject): Unit = extern
}
