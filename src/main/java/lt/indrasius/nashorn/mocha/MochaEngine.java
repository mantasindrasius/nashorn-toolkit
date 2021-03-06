package lt.indrasius.nashorn.mocha;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lt.indrasius.nashorn.EventLoop;
import lt.indrasius.nashorn.ScriptEngineBuilder;
import lt.indrasius.nashorn.exceptions.MochaEngineException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by mantas on 15.4.16.
 */
public class MochaEngine {
    private ScriptEngineBuilder nashornEngineBuilder = new ScriptEngineBuilder()
            .withEventLoop(new EventLoop())
            .withDOMFunctions()
            .withScriptFromClassPath("nashorn-mocha-js/mocha/mocha.js")
            .withLoadedScript("bower_components/chai/chai.js");

    private ScriptEngine nashornEngine;

    public MochaEngine(Consumer<ScriptEngineBuilder> engineSetup) {
        engineSetup.accept(nashornEngineBuilder);

        nashornEngine = nashornEngineBuilder.newEngine();
    }

    public Function<MochaListener, Object> configure(String[] specs) throws MochaEngineException {
        try {
            Reader reader = new InputStreamReader(getClass().getClassLoader()
                    .getResourceAsStream("nashorn-mocha-js/boot-mocha.js"));

            ScriptObjectMirror runner = (ScriptObjectMirror)
                    nashornEngine.eval(reader);

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
