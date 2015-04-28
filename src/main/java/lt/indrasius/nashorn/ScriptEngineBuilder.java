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
        loads.add(withEngine(engine -> DOMFunctions.bind(engine)));
        return this;
    }

    public ScriptEngineBuilder withLoadedScript(String filename) {
        loads.add(withEngine(engine -> loadScript(engine, filename)));
        return this;
    }

    public ScriptEngineBuilder withScriptFromClassPath(String path) {
        loads.add(withEngine(loadFromClassPath(path)));
        return this;
    }

    public ScriptEngineBuilder withObjectMapper(ObjectMapper mapper) {
        loads.add(withEngine(engine -> JSON.bindObjectMapper(engine, mapper)));
        return this;
    }

    public ScriptEngineBuilder withEventLoop(EventLoop manager) {
        loads.add(withEngine(engine -> engine.put("EventLoop", manager)));
        return this;
    }

    public ScriptEngineBuilder withFileSystemFunctions() {
        loads.add(withEngine(engine -> FileSystemFunctions.bind(engine)));
        return this;
    }

    public ScriptEngine newEngine() {
        ScriptEngineManager scriptManager = new ScriptEngineManager();
        ScriptEngine engine = scriptManager.getEngineByName("nashorn");

        loads.stream().forEach(load -> load.apply(engine));

        return engine;
    }

    private void loadScript(ScriptEngine engine, String filename) throws ScriptException {
        engine.eval("load('" + filename + "');");
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
