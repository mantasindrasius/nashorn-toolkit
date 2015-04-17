package lt.indrasius.nashorn

import java.nio.file.Files

import com.twitter.io.TempDirectory
import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

/**
 * Created by mantas on 15.4.10.
 */
class MochaTest extends SpecWithJUnit with Mockito {
  class Context extends Scope {
    val nashornEngine = EngineFactory.newEngine()
    val tempDir = TempDirectory.create(true)

    def givenFileExists(filename: String, content: String) = {
      val path = tempDir.toPath.resolve(filename)

      Files.write(path, content.getBytes)

      path.toString
    }

    nashornEngine must not(beNull)

    DOMFunctions.bind(nashornEngine)
  }

  "Mocha" should {
    "run a test and collect the events" in new Context {
      val filepath = givenFileExists("test.js",
        """var expect = chai.expect;
          |
          |function add(a, b) {
          | return a + b;
          |}
          |
          |describe("suite 1", function() {
          |  it("should pass", function() {
          |    expect(add(1, 2)).to.be.equal(3);
          |  });
          |
          |  it("should fail", function() {
          |    expect(add(1, 2)).to.be.equal(3);
          |    expect(add(1, 2)).to.be.equal(0);
          |  });
          |
          |  it("should pass with callback", function(done) {
          |    setTimeout(function() {
          |      expect(add(1, 2)).to.be.equal(3);
          |      done();
          |    }, 10);
          |  });
          |
          |  it("should pass with promise", function() {
          |    return new Promise(function(fulfill, reject) {
          |     setTimeout(function() {
          |        expect(add(1, 2)).to.be.equal(3);
          |        fulfill();
          |     }, 10);
          |    });
          |  });
          |
          |  xit("should be ignored", function() {
          |    expect(add(1, 2)).to.be.equal(3);
          |  });
          |})""".stripMargin)

      nashornEngine.eval("load('src/main/resources/nashorn-mocha-js/mocha/mocha.js');")
      nashornEngine.eval("load('bower_components/chai/chai.js');")
      nashornEngine.eval("load('bower_components/promise-js/promise.js');")

      val reporter = mock[MochaListener]

      nashornEngine.put("reporter", reporter)

      val runner = nashornEngine.eval("load('src/main/resources/nashorn-mocha-js/boot-mocha.js');").asInstanceOf[ScriptObjectMirror]

      nashornEngine.eval(s"load('$filepath');")

      val finalCallBack = nashornEngine.eval("function(res) { print('DONE', res); }")

      runner.callMember("run", finalCallBack)

      eventually {
        got {
          one(reporter).started(5)
          /*one(reporter).pass(===("should pass"), ===("suite 1 should pass"), any[Int])
          one(reporter).pass(===("should pass with promise"), ===("suite 1 should pass with promise"), any[Int])
          one(reporter).fail(===("should fail"), ===("suite 1 should fail"), any[Int], ===("expected 3 to equal 0"))
          one(reporter).pending("should be ignored", "suite 1 should be ignored")
          one(reporter).end(===(1), ===(5), ===(3), ===(1), ===(1), any[Int])
          exactly(4)(reporter).startTest(any[String], any[String])*/
        }
      }
    }
  }
}
