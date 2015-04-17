package lt.indrasius.nashorn

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecWithJUnit

/**
 * Created by mantas on 15.4.16.
 */
class MochaRuntimeTest extends SpecWithJUnit with Mockito {
  "MochaRuntime" should {
    "register a suite started event" in {
      val runtime = new MochaRuntime
      val specs = Array("mocha/test.js")

      val listener = mock[MochaListener]

      runtime.run(specs, listener)

      got {
        one(listener).started(1)
        one(listener).suiteStarted("suite 1")
        one(listener).testStarted("should pass", "suite 1 should pass")
        one(listener).testPassed(===("should pass"), ===("suite 1 should pass"), any)
        one(listener).suiteFinished("suite 1")
        one(listener).finished()

        noMoreCallsTo(listener)
      }
    }
  }
}
