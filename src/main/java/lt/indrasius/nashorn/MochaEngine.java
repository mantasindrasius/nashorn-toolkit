package lt.indrasius.nashorn;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lt.indrasius.nashorn.exceptions.MochaEngineException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by mantas on 15.4.16.
 */
public class MochaEngine {
    private ScriptEngine nashornEngine = EngineFactory.newEngine();

    public Function<MochaListener, Object> configure(String[] specs) throws MochaEngineException {
        try {
            DOMFunctions.bind(nashornEngine);

            nashornEngine.eval("load('src/main/resources/nashorn-mocha-js/mocha/mocha.js');");
            nashornEngine.eval("load('bower_components/chai/chai.js');");

            ScriptObjectMirror runner = (ScriptObjectMirror)
                    nashornEngine.eval("load('src/main/resources/nashorn-mocha-js/boot-mocha.js');");

            for (int i = 0; i < specs.length; i++) {
                String file = getClass().getClassLoader().getResource(specs[i]).getFile();

                nashornEngine.eval("load('" + file + "');");
            }

            return listener -> run(runner, listener);
        } catch (ScriptException e) {
            e.printStackTrace();

            throw MochaEngineException.apply(e.getMessage());
        }
    }

    private Object run(ScriptObjectMirror runner, MochaListener listener) {
        nashornEngine.put("reporter", listener);

        try {
            CompletableFuture promise = new CompletableFuture();

            nashornEngine.put("completionPromise", promise);

            ScriptObjectMirror finalCallBack = (ScriptObjectMirror)
                    nashornEngine.eval("function (res) { completionPromise.complete(null); }");

            runner.callMember("run", finalCallBack);

            return promise.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
