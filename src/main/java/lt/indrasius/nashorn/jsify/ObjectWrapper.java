package lt.indrasius.nashorn.jsify;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Created by mantas on 15.4.22.
 */
public class ObjectWrapper {
    private final ScriptEngine engine;
    private final JSWrapperGenerator generator;

    public ObjectWrapper(ScriptEngine engine, JSWrapperGenerator generator) {
        this.engine = engine;
        this.generator = generator;
    }
    
    public ScriptObjectMirror wrap(Object obj) throws ScriptException {
        String generated = generator.generate(obj);
        ScriptObjectMirror f = (ScriptObjectMirror) engine.eval(generated);

        return (ScriptObjectMirror) f.newObject(obj);
    }
}
