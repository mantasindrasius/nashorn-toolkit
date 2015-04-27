package lt.indrasius.nashorn.jsify

import lt.indrasius.nashorn.ScriptEngineBuilder
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

/**
 * Created by mantas on 15.4.22.
 */
class ObjectWrapperTest extends SpecWithJUnit with Mockito {
  class Context extends Scope {
    val engine = new ScriptEngineBuilder().newEngine()
    val generator = mock[JSWrapperGenerator]
    val target = mock[GreeterClass]
    val wrapper = new ObjectWrapper(engine, generator)
  }

  "ObjectWrapper" should {
    "return a function for a method" in new Context {
      generator.generate(target) returns "function(greeter) { this.greeting = greeter.greet('Hello'); }"
      target.greet("Hello") returns "World"

      val obj = wrapper.wrap(target)

      obj.getMember("greeting") must_== "World"
    }
  }
}
