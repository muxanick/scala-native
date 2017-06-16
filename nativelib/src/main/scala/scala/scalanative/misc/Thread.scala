package scala.scalanative.misc
package threads

import scala.collection.mutable
import scala.scalanative.native._

abstract class Thread extends Runnable {

    def run(): Unit
    
    def start(): Boolean = {
        thread = Thread.createThread(thread, threadId)
        thread != null;
    }

    def join():Unit = {
        Thread.threadJoin(thread)
    }

    override protected def finalize(): Unit = {
        join()
        Thread.threadFreeMemory(thread)
    }

    var thread: Thread.CThread = null // need to be aligned to 16 bytes
    val threadId = Thread.genId(this)
}

object Thread {
    type CThreadId = CInt
    type CThread = Ptr[CStruct0]
    type CThreadFunc1 = CFunctionPtr1[CThreadId, Unit]

    /// Private section

    def runCallback(threadId:CInt):Unit = {
        threads(threadId).run()
    }

    // compiler crash: (GenICode.scala:740) function's symbol is null
    val runCallBackPtr:CThreadFunc1 = null/*CFunctionPtr.fromFunction1(runCallback _)*/

    def createThread(thread: CThread, threadId: CInt): CThread = {
        Thread.threadStart(thread, runCallBackPtr, threadId)
    }

    // Utils
    private var nextId = 0;
    def genId(t: Thread):CThreadId = {
        threads(nextId) = t
val result = nextId
        nextId = nextId + 1
        result
    }

    var threads = new Array[Thread](256)

    // Bindings
    
    //def threadStart(thread: CThread, f: CFunctionPtr1[CThreadId, Unit], param: CThreadId): CThread = { println("threadStart"); thread }

    @name("scalanative_cpp_threadStart")
    def threadStart(thread: CThread, f: CFunctionPtr1[CThreadId, Unit], param: CThreadId): CThread = extern
    
    @name("scalanative_cpp_threadJoin")
    def threadJoin(thread: CThread):Unit = extern

    @name("scalanative_cpp_threadFreeMemory")
    def threadFreeMemory(thread: CThread):Unit = extern    
}

/*object CallbackFail {
  scala.scalanative.native.CFunctionPtr.fromFunction0(() => ())
}*/