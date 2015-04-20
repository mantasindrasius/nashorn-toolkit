package lt.indrasius.nashorn

import jdk.nashorn.api.scripting.ScriptObjectMirror
import lt.indrasius.nashorn.view.SimpleGetterClass
import org.specs2.matcher.{Matcher, Matchers}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.collection.convert.wrapAsJava.asJavaCollection

/**
 * Created by mantas on 15.4.19.
 */
class NashornObjectsTest extends SpecWithJUnit with Matchers {
  def beJSArray(values: AnyRef*): Matcher[AnyRef] =
    (haveArrayFlag and arrayHaveValues(values:_*)) ^^ { obj: AnyRef => obj.asInstanceOf[ScriptObjectMirror] aka "js array" }

  def haveArrayFlag: Matcher[ScriptObjectMirror] =
    beTrue ^^ { obj: ScriptObjectMirror => obj.isArray() aka "isArray flag" }

  def arrayHaveValues(values: AnyRef*): Matcher[ScriptObjectMirror] =
    containAllOf(values) ^^ { obj: ScriptObjectMirror => {
      val len = obj.getMember("length").asInstanceOf[Long].toInt

      Range(0, len) map { i => obj.getSlot(i) }
    } aka "has values" }

  class Context extends Scope {
    val engine = EngineFactory.newEngine()
  }

  "NashornObjects" should {
    "access an object field" in new Context {
      val obj = new NashornObjectView(new SimpleGetterClass("Hello World"))

      engine.put("obj", obj)
      engine.eval("obj.value") must_== "Hello World"
    }

    "iterate through all the fields" in new Context {
      val obj = new NashornObjectView(new SimpleGetterClass("Hello World"))

      engine.put("obj", obj)
      engine.eval(
        """var fields = []
          |
          |for (i in obj) fields.push(i);
          |
          |fields;
        """.stripMargin) must beJSArray("value")
    }

    "iterate through all the slots" in new Context {
      val arr = new NashornArrayView(Seq("a", "b", "c"))

      engine.put("arr", arr)
      engine.eval(
        """var values = []
          |
          |for (var i = 0; i < arr.length; i++) values.push(arr[i]);
          |
          |values;
        """.stripMargin) must beJSArray("a", "b" ,"c")
    }

    "pushing into array" in new Context {
      todo

      val arr = new NashornArrayView(Seq("a", "b", "c"))

      engine.put("arr", arr)
      engine.eval(
        """arr.push('d');
          |arr;
        """.stripMargin) must beJSArray("a", "b" ,"c", "d")
    }
  }
}
