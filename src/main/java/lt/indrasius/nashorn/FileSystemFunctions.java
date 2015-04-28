package lt.indrasius.nashorn;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lt.indrasius.nashorn.jsify.JSWrapperGenerator;
import lt.indrasius.nashorn.jsify.ObjectWrapper;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mantas on 15.4.28.
 */
public class FileSystemFunctions {
    public String readFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void bind(ScriptEngine engine) throws ScriptException {
        JSWrapperGenerator generator = new JSWrapperGenerator();
        ScriptObjectMirror wrapper = new ObjectWrapper(engine, generator).wrap(new FileSystemFunctions());

        engine.put("fs", wrapper);
    }
}
