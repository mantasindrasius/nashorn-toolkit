package lt.indrasius.nashorn

import com.fasterxml.jackson.databind.ObjectMapper
import lt.indrasius.nashorn.jsify.{ArrayView, ObjectView, SimpleGetterClass}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
import collection.convert.wrapAsJava.seqAsJavaList

/**
 * Created by mantas on 15.4.27.
 */
class JSONTest extends SpecWithJUnit {
  class Context extends Scope {
    val objectMapper = new ObjectMapper()

    val engine = new ScriptEngineBuilder()
      .newEngine()

    JSON.bindObjectMapper(engine, objectMapper)
  }

  "JSON" should {
    "serialize a raw bean object" in new Context {
      engine.put("target", new SimpleGetterClass[String]("Me"))
      engine.eval("JSON.stringify(target)") must_== """{"value":"Me"}"""
    }

    "serialize a view object" in new Context {
      engine.put("target", new ObjectView(new SimpleGetterClass[String]("Me")))
      engine.eval("JSON.stringify(target)") must_== """{"value":"Me"}"""
    }

    "serialize an array view object" in new Context {
      engine.put("target", new ArrayView(Seq("Hello")))
      engine.eval("JSON.stringify(target)") must_== """["Hello"]"""
    }

    "serialize a native object" in new Context {
      engine.eval("JSON.stringify({value:'Me'})") must_== """{"value":"Me"}"""
    }

    "serialize a native object array" in new Context {
      engine.eval("JSON.stringify([{value:'Me'}])") must_== """[{"value":"Me"}]"""
    }
  }
}
