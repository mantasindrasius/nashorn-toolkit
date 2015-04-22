package lt.indrasius.nashorn.mocha;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lt.indrasius.nashorn.DOMFunctions;
import lt.indrasius.nashorn.EngineFactory;
import lt.indrasius.nashorn.exceptions.MochaEngineException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

            List<String> notFounds = new ArrayList<String>();

            for (int i = 0; i < specs.length; i++) {
                String specPath = specs[i];
                URL url = getClass().getClassLoader().getResource(specPath);

                if (url == null)
                    notFounds.add(specPath);
                else {
                    String file = url.getFile();

                    nashornEngine.eval("load('" + file + "');");
                }
            }

            return notFounds.isEmpty() ?
                    listener -> run(runner, listener) :
                    listener -> {
                        listener.testFailed("mocha spec", "", 0L,
                                "specs not found: " + String.join(", ", notFounds), "");

                        return null;
                    };
        } catch (ScriptException e) {
            e.printStackTrace();

            throw new MochaEngineException(e.getMessage());
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

            return promise.get(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
