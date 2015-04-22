package lt.indrasius.nashorn.mocha;

import lt.indrasius.nashorn.exceptions.MochaEngineException;

/**
 * Created by mantas on 15.4.16.
 */
public class MochaRuntime {
    public void run(String[] specs, MochaListener listener) throws MochaEngineException {
        MochaEngine engine = new MochaEngine();

        engine.configure(specs).apply(listener);
    }
}
