package lt.indrasius.nashorn.jsify

import lt.indrasius.nashorn.ScriptEngineBuilder
import org.specs2.mutable.SpecWithJUnit

/**
 * Created by mantas on 15.5.1.
 */
class ArgumentAdapterTest extends SpecWithJUnit {
  "ArgumentAdapter" should {
    "do nothing to adapt the same type argument" in {
      ArgumentAdapter.adapt("Hello", classOf[String]) must_== "Hello"
    }

    "adapt String to an Integer" in {
      ArgumentAdapter.adapt("1234", classOf[java.lang.Integer]) must_== 1234
    }

    "adapt String to an Integer with String targetType" in {
      ArgumentAdapter.adapt("1234", "java.lang.Integer") must_== 1234
    }

    "adapt String to a primitive type with String targetType" in {
      ArgumentAdapter.adapt("1234", "int") must_== 1234
    }

    "adapt String to a Long" in {
      ArgumentAdapter.adapt("1234", classOf[java.lang.Long]) must_== 1234L
    }

    "adapt String to a Float" in {
      ArgumentAdapter.adapt("1234.45", classOf[java.lang.Float]) must_== 1234.45f
    }

    "adapt String to a Double" in {
      ArgumentAdapter.adapt("1234.45", classOf[java.lang.Double]) must_== 1234.45
    }

    "adapt String to a Boolean" in {
      ArgumentAdapter.adapt("true", classOf[java.lang.Boolean]) must_== true
    }

    "adapt object literal to a bean" in {
      val hello = new HelloBean()
      val engine = new ScriptEngineBuilder().newEngine()

      hello.setId(1234)
      hello.setName("World")

      val obj = engine.eval("""var obj = {"id":1234,"name":"World"}; obj""")

      ArgumentAdapter.adapt(obj, classOf[HelloBean]) must_== hello;
    }
  }
}
