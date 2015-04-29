package lt.indrasius.nashorn.jsify

import java.util.concurrent.Callable
import java.util.{List => JList}

import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.collection.convert.wrapAsJava.asJavaCollection
import scala.collection.convert.wrapAsScala.collectionAsScalaIterable

/**
 * Created by mantas on 15.4.19.
 */
class ArrayViewTest extends SpecWithJUnit {
  class Context[A](seq: Iterable[A]) extends Scope {
    val view = new ArrayView(seq)
  }

  "NashornArrayView" should {
    "isArray return true" in new Context(Seq(0)) {
      view.isArray must beTrue
    }

    "access slot 0" in new Context(Seq("Hello")) {
      view.getSlot(0) must_== "Hello"
    }

    "access last slot" in new Context(Seq("Hello", 1234)) {
      view.getSlot(1) must_== 1234
    }

    "fail to access beyond the last element" in new Context(Seq("Hello")) {
      view.getSlot(1) must throwAn[IllegalAccessError]
    }

    "answer hasSlot as true" in new Context(Seq("Hello")) {
      view.hasSlot(0) must beTrue
    }

    "return the correct length field" in new Context(Seq("Hello", 1234)) {
      view.getMember("length") must_== 2
    }

    "return length field as present" in new Context(Seq("Hello", 1234)) {
      view.hasMember("length") must beTrue
    }

    "set and get dynamic values" in new Context(Seq("Hello", 1234)) {
      view.setMember("abc", "Hello")
      view.getMember("abc") must_== "Hello"
    }

    "set and get dynamic values" in new Context(Seq("Hello", 1234)) {
      view.setMember("abc", "Hello")
      view.hasMember("abc") must beTrue
    }

    "return getTarget a callable" in new Context(Seq("Hello", 1234)) {
      val list: Iterable[Any] = view.getMember("getTarget").asInstanceOf[Callable[Array[AnyRef]]].call()

      list.toSeq must haveSize(2)
    }
  }
}
