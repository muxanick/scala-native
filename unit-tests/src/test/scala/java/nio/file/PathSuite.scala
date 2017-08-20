package java.nio.file

object PathSuite extends tests.Suite {

  test("Path.getNameCount()") {
    assert(Paths.get("/").getNameCount == 0)
    assert(Paths.get("///").getNameCount == 0)
    assert(Paths.get("").getNameCount == 1)
    assert(Paths.get("foo").getNameCount == 1)
    assert(Paths.get("foo//bar").getNameCount == 2)
    assert(Paths.get("foo/bar/baz").getNameCount == 3)
    assert(Paths.get("/foo/bar/baz").getNameCount == 3)
    assert(Paths.get("././").getNameCount == 2)
    assert(Paths.get("././ ").getNameCount == 3)
  }

  test("Path.getName") {
    assert(Paths.get("").getName(0).toString == "")
    assert(Paths.get("foo").getName(0).toString == "foo")
    assert(Paths.get("foo//bar").getName(0).toString == "foo")
    assert(Paths.get("foo//bar").getName(1).toString == "bar")

    assert(Paths.get("foo/bar/baz").getName(0).toString == "foo")
    assert(Paths.get("foo/bar/baz").getName(1).toString == "bar")
    assert(Paths.get("foo/bar/baz").getName(2).toString == "baz")

    assert(Paths.get("/foo/bar/baz").getName(0).toString == "foo")
    assert(Paths.get("/foo/bar/baz").getName(1).toString == "bar")
    assert(Paths.get("/foo/bar/baz").getName(2).toString == "baz")

  }

  test("Path.endsWith with absolute path") {
    assert(Paths.get("/foo/bar/baz").endsWith(Paths.get("baz")))
    assert(!Paths.get("/foo/bar/baz").endsWith(Paths.get("/baz")))
    assert(Paths.get("/foo/bar/baz").endsWith(Paths.get("bar/baz")))
    assert(!Paths.get("/foo/bar/baz").endsWith(Paths.get("/bar/baz")))
    assert(Paths.get("/foo/bar/baz").endsWith(Paths.get("foo/bar/baz")))
    assert(Paths.get("/foo/bar/baz").endsWith(Paths.get("/foo/bar/baz")))
  }

  test("Path.endsWith with relative path") {
    assert(Paths.get("foo/bar/baz").endsWith(Paths.get("baz")))
    assert(!Paths.get("foo/bar/baz").endsWith(Paths.get("/baz")))
    assert(Paths.get("foo/bar/baz").endsWith(Paths.get("bar/baz")))
    assert(!Paths.get("foo/bar/baz").endsWith(Paths.get("/bar/baz")))
    assert(Paths.get("foo/bar/baz").endsWith(Paths.get("foo/bar/baz")))
    assert(!Paths.get("foo/bar/baz").endsWith(Paths.get("/foo/bar/baz")))
  }

  test("Path.getFileName") {
    assert(Paths.get("").getFileName.toString == "")
    assert(Paths.get("foo").getFileName.toString == "foo")
    assert(Paths.get("/foo").getFileName.toString == "foo")
    assert(Paths.get("foo/bar").getFileName.toString == "bar")
    assert(Paths.get("/foo/bar").getFileName.toString == "bar")
    assert(Paths.get("/").getFileName == null)
    assert(Paths.get("///").getFileName == null)
  }

  test("Path.subpath") {
    assert(Paths.get("").subpath(0, 1).toString == "")
    assertThrows[IllegalArgumentException] {
      Paths.get("").subpath(0, 2)
    }

    assert(
      Paths.get("foo/bar/baz").subpath(0, 1).toString == Paths
        .get("foo")
        .toString)
    assert(
      Paths.get("foo/bar/baz").subpath(0, 2).toString == Paths
        .get("foo/bar")
        .toString)
    assert(
      Paths.get("foo/bar/baz").subpath(0, 3).toString == Paths
        .get("foo/bar/baz")
        .toString)
    assert(
      Paths.get("foo/bar/baz").subpath(1, 3).toString == Paths
        .get("bar/baz")
        .toString)
    assert(
      Paths.get("foo/bar/baz").subpath(2, 3).toString == Paths
        .get("baz")
        .toString)

    assert(
      Paths.get("/foo/bar/baz").subpath(0, 1).toString == Paths
        .get("foo")
        .toString)
    assert(
      Paths.get("/foo/bar/baz").subpath(0, 2).toString == Paths
        .get("foo/bar")
        .toString)
    assert(
      Paths.get("/foo/bar/baz").subpath(0, 3).toString == Paths
        .get("foo/bar/baz")
        .toString)
    assert(
      Paths.get("/foo/bar/baz").subpath(1, 3).toString == Paths
        .get("bar/baz")
        .toString)
    assert(
      Paths.get("/foo/bar/baz").subpath(2, 3).toString == Paths
        .get("baz")
        .toString)
  }

  test("Path.getParent") {
    assert(Paths.get("").getParent == null)
    assert(Paths.get("foo").getParent == null)
    assert(Paths.get("/").getParent == null)
    assert(Paths.get("//").getParent == null)
    assert(Paths.get("foo/bar").getParent.toString == Paths.get("foo").toString)
    assert(
      Paths.get("/foo/bar").getParent.toString == Paths.get("/foo").toString)
    assert(Paths.get("/foo").getParent.toString == Paths.get("/").toString)
    assert(Paths.get("foo/.").getParent.toString == Paths.get("foo").toString)
    assert(Paths.get("./.").getParent.toString == Paths.get(".").toString)
  }

  test("Path.getRoot") {
    assert(Paths.get("").getRoot == null)
    assert(Paths.get("foo").getRoot == null)
    assert(Paths.get("foo/bar").getRoot == null)
    assert(Paths.get("/foo").getRoot.toString == Paths.get("/").toString)
    assert(Paths.get("/foo/bar").getRoot.toString == Paths.get("/").toString)
    assert(Paths.get("/foo///bar").getRoot.toString == Paths.get("/").toString)
    assert(Paths.get("/").getRoot.toString == Paths.get("/").toString)
  }

  test("Path.isAbsolute") {
    assert(!Paths.get("").isAbsolute)
    assert(!Paths.get("foo").isAbsolute)
    assert(!Paths.get("foo/bar").isAbsolute)
    assert(Paths.get("/foo").isAbsolute)
    assert(Paths.get("/foo/bar").isAbsolute)
    assert(Paths.get("/foo///bar").isAbsolute)
    assert(Paths.get("/").isAbsolute)
  }

  test("Path.iterator") {
    import scala.language.implicitConversions
    implicit def iteratorToSeq[T: scala.reflect.ClassTag](
        it: java.util.Iterator[T]): Seq[T] = {
      import scala.collection.mutable.UnrolledBuffer
      val buf = new UnrolledBuffer[T]()
      while (it.hasNext) buf += it.next()
      buf
    }

    assert(Paths.get("").iterator.map(_.toString) == Seq(""))
    assert(Paths.get("foo").iterator.map(_.toString) == Seq("foo"))
    assert(Paths.get("foo/bar").iterator.map(_.toString) == Seq("foo", "bar"))
    assert(Paths.get("foo//bar").iterator.map(_.toString) == Seq("foo", "bar"))
    assert(Paths.get("/foo").iterator.map(_.toString) == Seq("foo"))
    assert(Paths.get("/foo/bar").iterator.map(_.toString) == Seq("foo", "bar"))
    assert(Paths.get("/foo//bar").iterator.map(_.toString) == Seq("foo", "bar"))
  }

  test("Path.normalize") {
    assert(Paths.get("").normalize.toString == "")
    assert(Paths.get("foo").normalize.toString == "foo")
    assert(
      Paths.get("foo/bar").normalize.toString == Paths.get("foo/bar").toString)
    assert(
      Paths.get("foo//bar").normalize.toString == Paths
        .get("foo/bar")
        .toString)
    assert(Paths.get("foo/../bar").normalize.toString == "bar")
    assert(
      Paths.get("foo/../../bar").normalize.toString == Paths
        .get("../bar")
        .toString)
    assert(
      Paths.get("/foo/../../bar").normalize.toString == Paths
        .get("/bar")
        .toString)
    assert(Paths.get("/").normalize.toString == Paths.get("/").toString)
    assert(Paths.get("/foo").normalize.toString == Paths.get("/foo").toString)
    assert(
      Paths.get("/foo/bar").normalize.toString == Paths
        .get("/foo/bar")
        .toString)
    assert(
      Paths.get("/foo//bar").normalize.toString == Paths
        .get("/foo/bar")
        .toString)
  }

  test("Path.startsWith") {
    assert(Paths.get("").startsWith(Paths.get("")))
    assert(Paths.get("foo").startsWith(Paths.get("foo")))
    assert(Paths.get("foo/bar").startsWith(Paths.get("foo")))
    assert(Paths.get("foo/bar/baz").startsWith(Paths.get("foo/bar")))
    assert(!Paths.get("foo").startsWith(Paths.get("bar")))
    assert(!Paths.get("foo/bar").startsWith(Paths.get("bar")))
    assert(!Paths.get("/").startsWith(Paths.get("")))
    assert(!Paths.get("").startsWith(Paths.get("/")))
    assert(Paths.get("/foo").startsWith(Paths.get("/")))
    assert(Paths.get("/foo/bar").startsWith(Paths.get("/foo")))
    assert(Paths.get("/").startsWith(Paths.get("/")))
    assert(!Paths.get("/").startsWith("/foo"))
  }

  test("Path.relativize") {
    assert(Paths.get("").relativize(Paths.get("")).toString == "")
    assert(Paths.get("foo").relativize(Paths.get("foo/bar")).toString == "bar")
    assert(Paths.get("foo/bar").relativize(Paths.get("foo")).toString == "..")
    assert(
      Paths.get("foo").relativize(Paths.get("bar")).toString == Paths
        .get("../bar")
        .toString)
    assert(
      Paths
        .get("foo/bar")
        .relativize(Paths.get("foo/baz"))
        .toString == Paths.get("../baz").toString)
    assert(Paths.get("").relativize(Paths.get("foo")).toString == "foo")
    assert(
      Paths
        .get("foo/../bar")
        .relativize(Paths.get("bar"))
        .toString == Paths.get("../../../bar").toString)

    assertThrows[IllegalArgumentException] {
      assert(Paths.get("/").relativize(Paths.get("")).toString == "")
    }

    assert(Paths.get("/").relativize(Paths.get("/")).toString == "")
    assert(
      Paths.get("/foo").relativize(Paths.get("/foo/bar")).toString == "bar")
    assert(Paths.get("/foo/bar").relativize(Paths.get("/foo")).toString == "..")
      Paths.get("/foo").relativize(Paths.get("/bar")).toString == Paths.get("../bar").toString)
    assert(
      Paths
        .get("/foo/bar")
        .relativize(Paths.get("/foo/baz"))
        .toString == Paths.get("../baz").toString)
    assert(Paths.get("/").relativize(Paths.get("/foo")).toString == "foo")
    assert(
      Paths
        .get("/foo/../bar")
        .relativize(Paths.get("/bar"))
        .toString == Paths.get("../../../bar").toString)
  }

  test("Path.resolve()") {
    assert(Paths.get("").resolve(Paths.get("")).toString == "")
    assert(
      Paths.get("/").resolve(Paths.get("")).toString == Paths
        .get("/")
        .toString)
    assert(
      Paths.get("foo").resolve(Paths.get("foo/bar")).toString == Paths
        .get("foo/foo/bar")
        .toString)
    assert(
      Paths.get("foo/bar").resolve(Paths.get("foo")).toString == Paths
        .get("foo/bar/foo")
        .toString)
    assert(
      Paths.get("foo").resolve(Paths.get("bar")).toString == Paths
        .get("foo/bar")
        .toString)
    assert(
      Paths
        .get("foo/bar")
        .resolve(Paths.get("foo/baz"))
        .toString == Paths.get("foo/bar/foo/baz").toString)
    assert(Paths.get("").resolve(Paths.get("foo")).toString == "foo")
    assert(
      Paths
        .get("foo/../bar")
        .resolve(Paths.get("bar"))
        .toString == Paths.get("foo/../bar/bar").toString)

    assert(
      Paths.get("/").resolve(Paths.get("/")).toString == Paths
        .get("/")
        .toString)
    assert(
      Paths.get("/foo").resolve(Paths.get("/foo/bar")).toString == Paths
        .get("/foo/bar")
        .toString)
    assert(
      Paths.get("/foo/bar").resolve(Paths.get("/foo")).toString == Paths
        .get("/foo")
        .toString)
    assert(
      Paths.get("/foo").resolve(Paths.get("/bar")).toString == Paths
        .get("/bar")
        .toString)
    assert(
      Paths
        .get("/foo/bar")
        .resolve(Paths.get("/foo/baz"))
        .toString == Paths.get("/foo/baz").toString)
    assert(
      Paths.get("/").resolve(Paths.get("/foo")).toString == Paths
        .get("/foo")
        .toString)
    assert(
      Paths.get("/foo/../bar").resolve(Paths.get("/bar")).toString == Paths
        .get("/bar")
        .toString)
  }

  test("Path.resolveSibling()") {
    assert(Paths.get("").resolveSibling(Paths.get("")).toString == "")
    assert(Paths.get("/").resolveSibling(Paths.get("")).toString == "")
    assert(
      Paths
        .get("foo")
        .resolveSibling(Paths.get("foo/bar"))
        .toString == Paths.get("foo/bar").toString)
    assert(
      Paths
        .get("foo/bar")
        .resolveSibling(Paths.get("foo"))
        .toString == Paths.get("foo/foo").toString)
    assert(Paths.get("foo").resolveSibling(Paths.get("bar")).toString == "bar")
    assert(
      Paths
        .get("foo/bar")
        .resolveSibling(Paths.get("foo/baz"))
        .toString == Paths.get("foo/foo/baz").toString)
    assert(Paths.get("").resolveSibling(Paths.get("foo")).toString == "foo")
    assert(
      Paths
        .get("foo/../bar")
        .resolveSibling(Paths.get("bar"))
        .toString == Paths.get("foo/../bar").toString)

    assert(
      Paths.get("/").resolveSibling(Paths.get("/")).toString == Paths
        .get("/")
        .toString)
    assert(
      Paths
        .get("/foo")
        .resolveSibling(Paths.get("/foo/bar"))
        .toString == Paths.get("/foo/bar").toString)
    assert(
      Paths
        .get("/foo/bar")
        .resolveSibling(Paths.get("/foo"))
        .toString == Paths.get("/foo").toString)
    assert(
      Paths.get("/foo").resolveSibling(Paths.get("/bar")).toString == Paths
        .get("/bar")
        .toString)
    assert(
      Paths
        .get("/foo/bar")
        .resolveSibling(Paths.get("/foo/baz"))
        .toString == Paths.get("/foo/baz").toString)
    assert(
      Paths.get("/").resolveSibling(Paths.get("/foo")).toString == Paths
        .get("/foo")
        .toString)
    assert(
      Paths
        .get("/foo/../bar")
        .resolveSibling(Paths.get("/bar"))
        .toString == Paths.get("/bar").toString)
  }
}
