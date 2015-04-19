package lt.indrasius.nashorn

import org.junit.runner.Description
import org.junit.runner.notification.{Failure, RunListener, RunNotifier}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * Created by mantas on 15.4.16.
 */
class MochaRunnerTest extends SpecificationWithJUnit with Mockito {
  @JSSpec(Array("mocha/test-pass.js")) class TestPass
  @JSSpec(Array("mocha/test-fail.js")) class TestFail
  @JSSpec(Array("mocha/test-ignore.js")) class TestIgnore
  @JSSpec(Array("mocha/test-multi-cases.js")) class TestMulti
  @JSSpec(Array("mocha/test-xyz-not-found.js")) class TestDoesNotExist

  class Context[A](val clazz: Class[A]) extends Scope {
    val runner = new MochaRunner(clazz)
    val listener = mock[RunListener]
    val notifier = new RunNotifier

    notifier.addListener(listener)
  }

  "MochaRunner" should {
    "run a passing test" in new Context(classOf[TestPass]) {
      val desc = Description.createTestDescription(clazz, "should pass")

      runner.run(notifier)

      got {
        one(listener).testStarted(desc)
        one(listener).testFinished(desc)
        noMoreCallsTo(listener)
      }
    }

    "run a failing test" in new Context(classOf[TestFail]) {
      val desc = Description.createTestDescription(clazz, "should fail")
      val assertionError = new AssertionError()
      val failure = new Failure(desc, assertionError)

      runner.run(notifier)

      got {
        one(listener).testStarted(desc)
        one(listener).testFailure(new Failure(desc, any))
        noMoreCallsTo(listener)
      }
    }

    "ignore a test" in new Context(classOf[TestIgnore]) {
      val desc = Description.createTestDescription(clazz, "should ignore")

      runner.run(notifier)

      got {
        one(listener).testIgnored(desc)
        noMoreCallsTo(listener)
      }
    }

    "run multiple tests" in new Context(classOf[TestMulti]) {
      runner.run(notifier)

      got {
        two(listener).testStarted(any)
        one(listener).testIgnored(any)
        one(listener).testFailure(any)
      }
    }

    "fail if spec not found" in new Context(classOf[TestDoesNotExist]) {
      runner.run(notifier)

      got {
        one(listener).testFailure(any)
        noMoreCallsTo(listener)
      }
    }
  }
}
