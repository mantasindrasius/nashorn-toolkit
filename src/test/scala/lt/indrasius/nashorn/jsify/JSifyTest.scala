package lt.indrasius.nashorn.jsify

import java.util.concurrent.TimeUnit

import jdk.nashorn.api.scripting.ScriptObjectMirror
import lt.indrasius.nashorn.{Promise, EventLoop, ScriptEngineBuilder}
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

    /*"pushing into array" in new Context {
      todo

      val arr = new ArrayView(Seq("a", "b", "c"))

      engine.put("arr", arr)
      engine.eval(
        """arr.push('d');
          |arr;
        """.stripMargin) must beJSArray("a", "b" ,"c", "d")
    }*/

    class WrapperContext extends Context {
      val scriptEngine = builder
        .withEventLoop(new EventLoop)
        .withDOMFunctions()
        .withLoadedScript("bower_components/promise-js/promise.js")
        .newEngine()

      val generator = new JSWrapperGenerator
      var wrapper = new ObjectWrapper(scriptEngine, generator).wrap(new TestService)

      scriptEngine.put("wrapper", wrapper)

    }

    "wrap an object method call into a promise" in new WrapperContext {
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

    "wrap a failed object method call into a promise" in new WrapperContext {
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

    "adapt the object literal argument into the expected type" in new WrapperContext {
      scriptEngine.eval(
        """result = 'F';
          |
          |var arg = { id: 1234, name: "World" };
          |
          |wrapper
          |  .sayHello(arg, 2)
          |  .then(function(str) {
          |    result = str;
          |  })
          |  .catch(function(err) {
          |    result = err;
          |  });
        """.stripMargin)

      eventually {
        scriptEngine.get("result") must_== "Hello World World (id: 1234)"
      }
    }

    "perform a filesystem read" in new Context {
      val scriptEngine = builder
        .withFileSystemFunctions()
        .withEventLoop(new EventLoop)
        .withDOMFunctions()
        .withLoadedScript("bower_components/promise-js/promise.js")
        .newEngine()

      val result = Promise.toFuture[String](scriptEngine.eval("fs.readFile('src/test/resources/test/hello.txt')"))

      result.get(100, TimeUnit.MILLISECONDS) must_== "Hello World!"
    }
  }
}
