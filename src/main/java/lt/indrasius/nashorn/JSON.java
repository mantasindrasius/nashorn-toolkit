package lt.indrasius.nashorn;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.api.scripting.JSObject;
import lt.indrasius.nashorn.jsify.ArrayView;
import lt.indrasius.nashorn.jsify.ObjectView;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Created by mantas on 15.4.27.
 */
public class JSON {
    private static String objViewClass = ObjectView.class.getName();
    private static String arrViewClass = ArrayView.class.getName();

    public static void bindObjectMapper(ScriptEngine engine, ObjectMapper mapper) throws ScriptException {
        JSObject wrapper = (JSObject) engine.eval("function(mapper) {" +
                "this._originalJSON = JSON;" +
                "this.parse = this._originalJSON.parse;" +
                "this.stringify = function(obj) {" +
                "  var target = obj;" +
                "  if (obj instanceof Java.type(\"" + objViewClass + "\")) target = obj.getTarget();" +
                "  else if (obj instanceof Java.type(\"" + arrViewClass + "\")) target = obj.getTarget();" +
                "  else if (Array.isArray(obj)) {" +
                "    return this._originalJSON.stringify(obj)" +
                "  }" +
                "  return mapper.writeValueAsString(target);" +
                "};}");

        Object wrapperObj = wrapper.newObject(mapper);
        engine.put("JSON", wrapperObj);

    }
}
