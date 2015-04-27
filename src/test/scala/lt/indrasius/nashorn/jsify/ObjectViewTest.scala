package lt.indrasius.nashorn.jsify

import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.collection.convert.wrapAsScala.asScalaSet

/**
 * Created by mantas on 15.4.19.
 */
class ObjectViewTest extends SpecWithJUnit {

  def beViewWithValue(value: AnyRef): Matcher[AnyRef] =
    beAnInstanceOf[ObjectView] and viewHasValue(value)

  def viewHasValue(value: AnyRef): Matcher[AnyRef] =
    be_===(value) ^^ { view: AnyRef => view.asInstanceOf[ObjectView].getTarget() }

  case class TestObj(test: String)

  class Context[A](val target: A) extends Scope {
    val view = new ObjectView(target)
  }

  "NashornObjectView" should {
    "has member should be true" in new Context(new SimpleGetterClass("Hello")) {
      view.hasMember("value") must beTrue
    }

    "get Boolean member" in new Context(new SimpleGetterClass(true)) {
      view.getMember("value") must_== true
    }

    "get String member" in new Context(new SimpleGetterClass("Hello")) {
      view.getMember("value") must_== "Hello"
    }

    "get Int member" in new Context(new SimpleGetterClass(1234)) {
      view.getMember("value") must_== 1234
    }

    "get Long member" in new Context(new SimpleGetterClass(123467890123455L)) {
      view.getMember("value") must_== 123467890123455L
    }

    "get Float member" in new Context(new SimpleGetterClass(1234.567)) {
      view.getMember("value") must_== 1234.567
    }

    "get Double member" in new Context(new SimpleGetterClass(12345678901234.567d)) {
      view.getMember("value") must_== 12345678901234.567d
    }

    "get Object member wrapped in View" in new Context(new SimpleGetterClass(TestObj("test"))) {
      view.getMember("value") must beViewWithValue(TestObj("test"))
    }

    "get null member" in new Context(new SimpleGetterClass(null)) {
      view.getMember("value") must beNull
    }

    "get Array member wrapped in ArrayView" in new Context(new SimpleGetterClass(Array("test"))) {
      view.getMember("value") must beAnInstanceOf[ArrayView]
    }

    "get Collection member wrapped in ArrayView" in new Context(new SimpleGetterClass(java.util.Arrays.asList("test"))) {
      view.getMember("value") must beAnInstanceOf[ArrayView]
    }

    "set member value" in new Context(new SimpleGetSetClass()) {
      view.setMember("value", "Hello")
      view.getMember("value") must_== "Hello"
    }

    "fail to set member on only get field" in new Context(new SimpleGetterClass("test")) {
      view.setMember("value", "Hello") must throwAn[IllegalAccessError](message = "Object has no value setter")
    }

    "set a member on setter-only field" in new Context(new SimpleSetterClass[String]()) {
      view.setMember("value", "Hello")

      target.value must_== "Hello"
    }

    "fail to set a member on setter that does not exist" in new Context(new SimpleSetterClass[String]()) {
      view.setMember("xyz", "Hello") must throwAn[IllegalAccessError]
    }

    "get object keys" in new Context(new KeyGetSetClass()) {
      asScalaSet(view.keySet()) must contain("x", "y", "test").exactly
    }
  }
}
