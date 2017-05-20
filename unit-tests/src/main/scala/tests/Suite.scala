package tests

import scala.collection.mutable
import scala.reflect.ClassTag

final case class AssertionFailed(message: String = null) extends Exception

final case class Test(name: String, run: () => (Boolean, String))

abstract class Suite {
  private val tests = new mutable.UnrolledBuffer[Test]

  def assert(cond: Boolean): Unit =
    if (!cond) throw new AssertionFailed else ()

  def assert(cond: Boolean, message: => Any): Unit =
    if (!cond) throw new AssertionFailed("assertion failed: " + message)
    else ()

  def assertNot(cond: Boolean): Unit =
    if (cond) throw new AssertionFailed else ()

  def assertNot(cond: Boolean, message: => Any): Unit =
    if (cond) throw new AssertionFailed("assetionNot failed: " + message)
    else ()

  def assertThrowsAnd[T: ClassTag](f: => Unit)(fe: T => Boolean): Unit = {
    try {
      f
    } catch {
      case exc: Throwable =>
        if (exc.getClass.equals(implicitly[ClassTag[T]].runtimeClass) &&
            fe(exc.asInstanceOf[T]))
          return
        else
          throw new AssertionFailed
    }
    throw new AssertionFailed
  }

  def assertThrows[T: ClassTag](f: => Unit): Unit =
    assertThrowsAnd[T](f)(_ => true)

  def assertEquals[T](left: T, right: T, message: => Any = null): Unit =
    assert(left == right,
           s"'${right}' is not equal to '${left}' : " + (if (message != null)
                                                           message
                                                         else ""))

  private def assertThrowsImpl(cls: Class[_], f: => Unit): Unit = {
    try {
      f
    } catch {
      case exc: Throwable =>
        if (exc.getClass.equals(cls))
          return
        else
          throw new AssertionFailed
    }
    throw new AssertionFailed
  }

  def expectThrows[T <: Throwable, U](expectedThrowable: Class[T],
                                      code: => U): Unit =
    assertThrowsImpl(expectedThrowable, code)

  def test(name: String)(body: => Unit): Unit =
    tests += Test(
      name, { () =>
        try {
          body
          (true, null)
        } catch {
          case a: AssertionFailed => (false, a.message)
          case b: Throwable => {
            val sw = new java.io.StringWriter
            val pw = new java.io.PrintWriter(sw)
            b.printStackTrace(pw)
            (false, sw.toString)
          }
        }
      }
    )

  def testFails(name: String, issue: Int)(body: => Unit): Unit =
    tests += Test(name, { () =>
      try {
        body
        (false, null)
      } catch {
        case _: Throwable => (true, null)
      }
    })

  def run(): Boolean = {
    println("* " + this.getClass.getName)
    var success = true

    tests.foreach { test =>
      val testSuccess = test.run()
      val status      = if (testSuccess._1) "  [ok] " else "  [fail] "
      println(status + test.name)
      if (testSuccess._2 != null) println("       " + testSuccess._2)
      success = success && testSuccess._1
    }

    success
  }
}
