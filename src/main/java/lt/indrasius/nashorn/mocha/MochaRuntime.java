package lt.indrasius.nashorn.mocha;

import lt.indrasius.nashorn.ScriptEngineBuilder;
import lt.indrasius.nashorn.exceptions.MochaEngineException;

import java.util.function.Consumer;

/**
 * Created by mantas on 15.4.16.
 */
public class MochaRuntime {
    public void run(String[] specs, MochaListener listener) throws MochaEngineException {
        run(specs, listener, scriptEngineBuilder -> {});
    }

    public void run(String[] specs, MochaListener listener, Consumer<ScriptEngineBuilder> engineSetup) throws MochaEngineException {
        MochaEngine engine = new MochaEngine(engineSetup);

        engine.configure(specs).apply(listener);
    }
}
