package lt.indrasius.nashorn;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by mantas on 15.4.10.
 */
public class EngineFactory {
    public static ScriptEngine newEngine() {
        ScriptEngineManager scriptManager = new ScriptEngineManager();

        return scriptManager.getEngineByName("nashorn");
    }
}
