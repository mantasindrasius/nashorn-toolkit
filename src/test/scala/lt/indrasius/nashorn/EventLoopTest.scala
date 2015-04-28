package lt.indrasius.nashorn

import java.util.concurrent.Callable
import java.util.function.Consumer

import lt.indrasius.nashorn.jsify.TestService
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
;

/**
 * Created by mantas on 15.4.27.
 */
class EventLoopTest extends SpecWithJUnit with Mockito {
  type LongFunction = Long => Unit

  def callableOf(f: => AnyRef) =
    new Callable[AnyRef] {
      def call(): AnyRef = f
    }

  class Context extends Scope {
    val loop = new EventLoop()
    val testService = new TestService
  }

  "AsyncManager" should {
    "execute a task and callback success" in new Context {
      val reject = mock[Consumer[Throwable]]
      val fulfill = mock[Consumer[Object]]

      loop.unblock(callableOf(new TestService().blockingMethod()), reject, fulfill)

      eventually {
        got {
          one(fulfill).accept(any)
          noCallsTo(reject)
        }
      }
    }

    "schedule a task to run" in new Context {
      val task = mock[Runnable]

      loop.schedule(task, 110)

      no(task).run()

      eventually {
        got {
          one(task).run()
        }
      }
    }
  }
}
