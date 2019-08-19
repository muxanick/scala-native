package java.lang

import scalanative.annotation.stub

class ClassLoader protected (parent: ClassLoader) {
  def this() = this(null)
  
  def loadClass(name: String): Class[_] = {
    new Exception("Not Implemented!")
    null
  }
  
  def getParent(): ClassLoader = {
    new Exception("Not Implemented!")
    null
  }
  
  def getResourceAsStream(name: String): java.io.InputStream = {
    new Exception("Not Implemented!")
    null
  }
  
  def getResources(name: String): java.util.Enumeration[_] = {
    new Exception("Not Implemented!")
    null
  }
}
