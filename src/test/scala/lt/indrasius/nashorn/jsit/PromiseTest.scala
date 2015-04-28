package lt.indrasius.nashorn.jsit

import lt.indrasius.nashorn.{EventLoop, ScriptEngineBuilder}
import lt.indrasius.nashorn.jsify.TestService
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

/**
 * Created by mantas on 15.4.26.
 */
class PromiseTest extends SpecWithJUnit {
  class Context extends Scope {
    val engine = new ScriptEngineBuilder()
      .withEventLoop(new EventLoop)
      .withDOMFunctions()
      .withLoadedScript("bower_components/promise-js/promise.js")
      .newEngine()
  }

  "promise" should {
    "return a resolved result" in new Context {
      engine.eval(
        "result = 'F';" +
        "Promise.resolve('Hello').then(function(r){ result = r; });")

      eventually {
        engine.get("result") must_== "Hello"
      }
    }

    "return a result with a blocking method" in new Context {
      engine.put("target", new TestService)

      engine.eval(
        "result = '';" +
          "new Promise(function(fulfill, reject) { try{fulfill(target.blockingMethod());}catch(e){reject(e);} }).then(function(r){ result = r; });" +
          "result = 'F';")

      eventually {
        engine.get("result") must_== "Hello"
      }
    }
  }
}
