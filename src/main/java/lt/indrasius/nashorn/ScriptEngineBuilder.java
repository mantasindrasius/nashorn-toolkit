package lt.indrasius.nashorn;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * Created by mantas on 15.4.10.
 */
public class ScriptEngineBuilder {
    private LinkedList<Function<ScriptEngine, ScriptEngine>> loads = new LinkedList<>();

    @FunctionalInterface
    public interface ConsumerThatThrows<T> {
        void accept(T t) throws ScriptException;
    }

    public ScriptEngineBuilder withDOMFunctions() {
        loads.add(engine -> bindDOMFunctions(engine));
        return this;
    }

    public ScriptEngineBuilder withLoadedScript(String filename) {
        loads.add(engine -> loadScript(engine, filename));
        return this;
    }

    public ScriptEngineBuilder withScriptFromClassPath(String path) {
        loads.add(withEngine(loadFromClassPath(path)));
        return this;
    }

    public ScriptEngineBuilder withObjectMapper(ObjectMapper mapper) {
        loads.add(engine -> bindObjectMapper(engine, mapper));
        return this;
    }

    public ScriptEngineBuilder withEventLoop(EventLoop manager) {
        loads.add(engine -> bindEventLoop(engine, manager));
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

    private ConsumerThatThrows<ScriptEngine> loadFromClassPath(String resourcePath) {
        return engine -> {
            URL resource = getClass().getClassLoader().getResource(resourcePath);
            File resourceFile = new File(resource.getPath());

            if (resourceFile.exists())
                loadScript(engine, resourceFile.getAbsolutePath());
            else {
                Reader in = new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(resourcePath));

                engine.eval(in);
            }
        };
    }

    private ScriptEngine bindObjectMapper(ScriptEngine engine, ObjectMapper mapper) {
        try {
            JSON.bindObjectMapper(engine, mapper);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return engine;
    }

    private ScriptEngine bindEventLoop(ScriptEngine engine, EventLoop manager) {
        engine.put("EventLoop", manager);

        return engine;
    }

    private Function<ScriptEngine, ScriptEngine> withEngine(ConsumerThatThrows<ScriptEngine> consumer) {
        return engine -> {
            try {
                consumer.accept(engine);
            } catch (ScriptException e) {
                e.printStackTrace();
            }

            return engine;
        };
    }
}
