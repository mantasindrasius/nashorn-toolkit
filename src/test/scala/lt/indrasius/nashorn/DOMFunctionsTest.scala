package lt.indrasius.nashorn

import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
import java.lang.{Integer => JInteger}

/**
 * Created by mantas on 15.4.10.
 */
class DOMFunctionsTest extends SpecWithJUnit {
  sequential

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

    "bind setTimeout functions" in new Context {
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

    "bind setInterval functions" in new Context {
      DOMFunctions.bind(engine)

      engine.eval(
        """var count1 = 0, count2 = 0;
          |var done = false;
          |var handle1 = setInterval(function() { count1++; }, 20)
          |clearInterval(handle1);
          |
          |var handle2 = setInterval(function() { count2++; }, 10)
          |setTimeout(function() { clearInterval(handle2); done = true; }, 50);""".stripMargin)

      eventually {
        engine.get("done") must_== true
        engine.get("count1") must_== 0
        engine.get("count2").asInstanceOf[Double] must be_>=(5d)
      }
    }
  }
}
