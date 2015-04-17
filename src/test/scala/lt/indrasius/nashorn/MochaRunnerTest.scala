package lt.indrasius.nashorn

import org.junit.runner.Description
import org.junit.runner.notification.{RunListener, RunNotifier}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * Created by mantas on 15.4.16.
 */
class MochaRunnerTest extends SpecificationWithJUnit with Mockito {
  @JSSpec(Array("mocha/test.js")) class TestClass

  class Context extends Scope {
    val runner = new MochaRunner(classOf[TestClass])
    val listener = mock[RunListener]
    val notifier = new RunNotifier

    notifier.addListener(listener)
  }

  "MochaRunner" should {
    "run a test suite" in new Context {
      val desc = Description.createTestDescription(classOf[TestClass], "should pass")

      runner.run(notifier)

      got {
        one(listener).testStarted(desc)
        noMoreCallsTo(listener)
      }
    }
  }
}
