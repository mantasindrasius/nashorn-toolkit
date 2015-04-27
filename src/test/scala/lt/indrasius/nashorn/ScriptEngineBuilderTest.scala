package lt.indrasius.nashorn

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

      engine.get("someVar") must_== "Hello";
    }
  }
}
