package lt.indrasius.nashorn

import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

/**
 * Created by mantas on 15.4.10.
 */
class DOMFunctionsTest extends SpecWithJUnit {
  class Context extends Scope {
    val functions = new DOMFunctions
    val engine = new ScriptEngineBuilder()
      .withEventLoop(new EventLoop)
      .newEngine()
  }

  "DOMFunctions" should {
    "execute setTimeout" in new Context {
      engine.put("result", "")

      val cb = engine.eval("function callback() { result = 'OK'; }").asInstanceOf[ScriptObjectMirror]

      functions.setTimeout(cb, 10)

      eventually {
        engine.get("result") must_== "OK"
      }
    }

    "clear timeout" in new Context {
      engine.put("result", "")

      val cb = engine.eval("function callback() { result = 'ERROR'; }").asInstanceOf[ScriptObjectMirror]
      val checkerCB = engine.eval("function callback2() { result = result === 'ERROR' ? result : 'OK'; }").asInstanceOf[ScriptObjectMirror]

      val ref = functions.setTimeout(cb, 50)
      functions.setTimeout(checkerCB, 60)

      functions.clearTimeout(ref)

      eventually {
        engine.get("result") must_== "OK"
      }
    }

    "bind functions" in new Context {
      DOMFunctions.bind(engine)

      engine.eval(
        """var result = '';
          |var handle = setTimeout(function() { result = 'FAIL'; }, 50)
          |setTimeout(function() { result = result === '' ? 'OK' : ''; }, 60)
          |clearTimeout(handle);""".stripMargin)

      eventually {
        engine.get("result") must_== "OK"
      }
    }
  }
}
