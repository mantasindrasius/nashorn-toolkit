package lt.indrasius.nashorn.jsify

import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

/**
 * Created by mantas on 15.4.26.
 */
class JSWrapperGeneratorTest extends SpecWithJUnit {
  class Context extends Scope {
    val generator = new JSWrapperGenerator
  }

  "JSWrapperGenerator" should {
    "generate empty class with no methods" in new Context {
      generator.generate(new EmptyClass) must {
        contain("function(target){") and
        contain("var ObjectView = Java.type('") and
        contain("var _argc = Java.type('")
      }
    }

    "generate a wrapper for parameter-less method" in new Context {
      class Target {
        def hello(): String = "Hello"
      }

      generator.generate(new Target) must
        contain("this.hello = function(){")
    }

    "generate a wrapper with inherited method" in new Context {
      class Base {
        def hello(): String = "Hello"
      }

      class Target extends Base

      generator.generate(new Target) must contain("this.hello = function(){")
    }

    "generate single parameter method that returns a promise" in new Context {
      val greeter = new GreeterClass
      val greeterMethod = greeter.getClass.getMethod("greet", classOf[String])

      val argsBlock = """_argc.adapt(arg0,"java.lang.String")""";
      val execBlock = s"EventLoop.unblock(function(){return ObjectView.wrap(target.greet($argsBlock));},reject,fulfill);"

      generator.generateMethod(greeterMethod) must contain(
        s"this.greet = function(arg0){return new Promise(function(fulfill,reject){$execBlock});};\n")
    }
  }
}
