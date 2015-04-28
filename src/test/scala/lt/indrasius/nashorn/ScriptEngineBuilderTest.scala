package lt.indrasius.nashorn

import com.fasterxml.jackson.databind.ObjectMapper
import lt.indrasius.nashorn.jsify.SimpleGetSetClass
import org.specs2.mutable.SpecWithJUnit

/**
 * Created by mantas on 15.4.26.
 */
class ScriptEngineBuilderTest extends SpecWithJUnit {
  "ScriptEngineBuilder" should {
    "create engine with DOMFunctions loaded" in {
      val engine = new ScriptEngineBuilder()
        .withDOMFunctions()
        .newEngine()

      engine.get("setTimeout") must not(beNull)
    }

    "create engine with file script loaded" in {
      val file = getClass.getClassLoader.getResource("test/define.js").getPath

      val engine = new ScriptEngineBuilder()
        .withLoadedScript(file)
        .newEngine()

      engine.get("someVar") must_== "Hello"
    }

    "create engine with file script loaded for the classpath" in {
      val engine = new ScriptEngineBuilder()
        .withScriptFromClassPath("test/define.js")
        .newEngine()

      engine.get("someVar") must_== "Hello"
    }

    "create engine with JSON override" in {
      val mapper = new ObjectMapper()
      val target = new SimpleGetSetClass[String]()

      target.setValue("Hello")

      val engine = new ScriptEngineBuilder()
        .withObjectMapper(mapper)
        .newEngine()

      engine.put("target", target)
      engine.eval("JSON.parse(JSON.stringify(target)).value") must_== "Hello"
    }

    "create engine with JSON override" in {
      val mapper = new ObjectMapper()
      val target = new SimpleGetSetClass[String]()

      target.setValue("Hello")

      val engine = new ScriptEngineBuilder()
        .withObjectMapper(mapper)
        .newEngine()

      engine.put("target", target)
      engine.eval("JSON.parse(JSON.stringify(target)).value") must_== "Hello"
    }

    "create engine with EventLoop" in {
      val engine = new ScriptEngineBuilder()
        .withEventLoop(new EventLoop)
        .newEngine()

      engine.eval("EventLoop") must not(beNull)
    }
  }
}
