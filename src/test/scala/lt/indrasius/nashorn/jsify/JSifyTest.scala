package lt.indrasius.nashorn.jsify

import jdk.nashorn.api.scripting.ScriptObjectMirror
import lt.indrasius.nashorn.ScriptEngineBuilder
import org.specs2.matcher.{Matcher, Matchers}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.collection.convert.wrapAsJava.asJavaCollection

/**
 * Created by mantas on 15.4.19.
 */
class JSifyTest extends SpecWithJUnit with Matchers {
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
    val builder = new ScriptEngineBuilder()
    val engine = builder.newEngine()
  }

  "JSify" should {
    "access an object field" in new Context {
      val obj = new ObjectView(new SimpleGetterClass("Hello World"))

      engine.put("obj", obj)
      engine.eval("obj.value") must_== "Hello World"
    }

    "iterate through all the fields" in new Context {
      val obj = new ObjectView(new SimpleGetterClass("Hello World"))

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
      val arr = new ArrayView(Seq("a", "b", "c"))

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

      val arr = new ArrayView(Seq("a", "b", "c"))

      engine.put("arr", arr)
      engine.eval(
        """arr.push('d');
          |arr;
        """.stripMargin) must beJSArray("a", "b" ,"c", "d")
    }

    "wrap an object method call into a promise" in new Context {
      val scriptEngine = builder
        .withDOMFunctions()
        .withLoadedScript("bower_components/promise-js/promise.js")
        .newEngine()

      val generator = new JSWrapperGenerator
      var wrapper = new ObjectWrapper(scriptEngine, generator).wrap(new TestService)

      scriptEngine.put("wrapper", wrapper)

      scriptEngine.eval(
        """result = 'F';
          |
          |wrapper
          |  .blockingMethod()
          |  .then(function(str) {
          |    result = str;
          |  });
        """.stripMargin)

      eventually {
        scriptEngine.get("result") must_== "Hello"
      }
    }

    "wrap a failed object method call into a promise" in new Context {
      val scriptEngine = builder
        .withDOMFunctions()
        .withLoadedScript("bower_components/promise-js/promise.js")
        .newEngine()

      val generator = new JSWrapperGenerator
      var wrapper = new ObjectWrapper(scriptEngine, generator).wrap(new TestService)

      scriptEngine.put("wrapper", wrapper)

      scriptEngine.eval(
        """result = 'F';
          |
          |wrapper
          |  .blockingFailMethod()
          |  .catch(function(err) {
          |    result = 'ERR';
          |  });
        """.stripMargin)

      eventually {
        scriptEngine.get("result") must_== "ERR"
      }
    }
  }
}
