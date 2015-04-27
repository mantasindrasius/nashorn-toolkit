package lt.indrasius.nashorn;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * Created by mantas on 15.4.10.
 */
public class ScriptEngineBuilder {
    private LinkedList<Function<ScriptEngine, ScriptEngine>> loads = new LinkedList<>();

    public ScriptEngineBuilder withDOMFunctions() {
        loads.add(engine -> bindDOMFunctions(engine));
        return this;
    }

    public ScriptEngineBuilder withLoadedScript(String filename) {
        loads.add(engine -> loadScript(engine, filename));
        return this;
    }

    public ScriptEngine newEngine() {
        ScriptEngineManager scriptManager = new ScriptEngineManager();
        ScriptEngine engine = scriptManager.getEngineByName("nashorn");

        loads.stream().forEach(load -> load.apply(engine));

        return engine;
    }

    private ScriptEngine bindDOMFunctions(ScriptEngine engine) {
        try {
            DOMFunctions.bind(engine);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return engine;
    }

    private ScriptEngine loadScript(ScriptEngine engine, String filename) {
        try {
            engine.eval("load('" + filename + "');");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return engine;
    }
}
