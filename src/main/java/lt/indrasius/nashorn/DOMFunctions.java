package lt.indrasius.nashorn;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mantas on 15.4.10.
 */
public class DOMFunctions {
    //private ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    //private int units = Runtime.getRuntime().availableProcessors() * 2;
    private int units = 1;
    private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(units);

    public TimeoutRef setTimeout(ScriptObjectMirror handler, Integer timeout) {
        Future f = scheduled.schedule(() -> handler.call(handler), timeout, TimeUnit.MILLISECONDS);

        return new TimeoutRef(f);
    }

    public void clearTimeout(Object timeout) {
        if (timeout instanceof TimeoutRef) {
            ((TimeoutRef)timeout).clear();
        } else {
            System.err.println("cannot clear type " + timeout.getClass().getName());
        }
    }

    public static void bind(ScriptEngine engine) throws ScriptException {
        String setTimeoutBody = "function(handler, timeout) {" +
                "  return functions.setTimeout(handler, timeout); " +
                "};\n";

        String clearTimeoutBody = "function(handle) {" +
                "  functions.clearTimeout(handle); " +
                "};\n";

        String scriptBody = "(function(global) {\n" +
                "  var DOMFunctions = Java.type('" + DOMFunctions.class.getName() + "');\n" +
                "  var functions = new DOMFunctions();\n" +
                "  global.setTimeout = " + setTimeoutBody +
                "  global.clearTimeout = " + clearTimeoutBody +
                "})(this)";

        engine.eval(scriptBody);
    }
}

class TimeoutRef {
    private Future handle;

    public TimeoutRef(Future f) {
        handle = f;
    }

    public void clear() {
        handle.cancel(true);
    }
}