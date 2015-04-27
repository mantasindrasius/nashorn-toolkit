package lt.indrasius.nashorn

import java.util.concurrent.TimeUnit

import lt.indrasius.nashorn.jsify.TestService
import org.specs2.mutable.SpecWithJUnit

/**
 * Created by mantas on 15.4.26.
 */
class PromiseTest extends SpecWithJUnit {
  "Promise" should {
    "convert a promise to the future" in {
      val engine = new ScriptEngineBuilder()
        .withDOMFunctions()
        .withLoadedScript("bower_components/promise-js/promise.js")
        .newEngine()

      engine.put("target", new TestService)
      val promise = engine.eval("new Promise(function(fulfill, reject) { fulfill(target.blockingMethod()); }); ")

      Promise.toFuture[String](promise).get(200, TimeUnit.MILLISECONDS) must_== "Hello"
    }
  }
}
