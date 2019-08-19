import scalanative.unsafe._

object Test {
  def myprintln(s: String): Int = {
    val l = stackalloc[Byte](100)
    println(s)
    s.length
  }
  def main(args: Array[String]): Unit = {
    myprintln("Hello ScaNa!")
  }
}
