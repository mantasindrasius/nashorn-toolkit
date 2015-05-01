package lt.indrasius.nashorn.jsify

import java.util.concurrent.Callable

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

    "get getTarget method" in new Context(new SimpleGetterClass("Hello")) {
      view.getMember("getTarget").asInstanceOf[Callable[SimpleGetterClass[String]]].call() must
        beAnInstanceOf[SimpleGetterClass[String]]
    }

    "fail to get an unexisting member" in new Context(new SimpleGetterClass(null)) {
      view.getMember("xyz") must beNull
    }

    "set member value" in new Context(new SimpleGetSetClass()) {
      view.setMember("value", "Hello")
      view.getMember("value") must_== "Hello"
    }

    "set and get unexisting value" in new Context(new SimpleGetSetClass()) {
      view.setMember("xyz", "Hello")
      view.getMember("xyz") must_== "Hello"
    }

    "set and check unexisting value" in new Context(new SimpleGetSetClass()) {
      view.setMember("xyz", "Hello")
      view.hasMember("xyz") must beTrue
    }

    "fail to set member on only get field" in new Context(new SimpleGetterClass("test")) {
      view.setMember("value", "Hello") must throwAn[IllegalAccessError](message = "Object has no value setter")
    }

    "set a member on setter-only field" in new Context(new SimpleSetterClass[String]()) {
      view.setMember("value", "Hello")

      target.value must_== "Hello"
    }

    "tell Array member is array" in new Context(new SimpleArrayGetterClass(Array("test"))) {
      view.isMemberArray("values") must beTrue
    }

    "tell the type of the Array members" in new Context(new HelloBeanGetterClass(new HelloBean())) {
      view.getArrayMemberType("values") must_== classOf[HelloBean]
    }

    "get object keys" in new Context(new KeyGetSetClass()) {
      asScalaSet(view.keySet()) must contain("x", "y", "test").exactly
    }

    "wrap Object" in new Context(new SimpleGetterClass("x")) {
      ObjectView.wrap(target) must beAnInstanceOf[ObjectView]
    }

    "do not wrap String" in {
      ObjectView.wrap("Hello") must_== "Hello"
    }

    "do not wrap Integer" in {
      ObjectView.wrap(12345) must_== 12345
    }
  }
}
