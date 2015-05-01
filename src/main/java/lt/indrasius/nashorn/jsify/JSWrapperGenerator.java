package lt.indrasius.nashorn.jsify;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by mantas on 15.4.22.
 */
public class JSWrapperGenerator {
    public String generate(Object target) {
        StringBuilder sb = new StringBuilder();
        Method[] methods = target.getClass().getMethods();

        sb.append("function(target){");
        sb.append("var ObjectView = Java.type('" + ObjectView.class.getName() + "');");
        sb.append("var _argc = Java.type('" + ArgumentAdapter.class.getName() + "');");

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            if (!method.getDeclaringClass().equals(Object.class))
                sb.append(generateMethod(method));
        }

        sb.append("}");

        return sb.toString();
    }

    public String generateMethod(Method method) {
        StringBuilder sb = new StringBuilder("this." + method.getName() + " = function(");

        Parameter[] parameters = method.getParameters();
        String[] names = new String[parameters.length];
        String[] adaptations = new String[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String clz = parameters[i].getType().getName();
            names[i] = parameters[i].getName();
            adaptations[i] = "_argc.adapt(" + parameters[i].getName() + ",\"" + clz + "\")";
        }

        sb.append(String.join(",", names));

        sb.append("){");
        sb.append("return new Promise(function(fulfill,reject){");
        sb.append("EventLoop.unblock(function(){return ObjectView.wrap(target.");
        sb.append(method.getName());
        sb.append("(");
        sb.append(String.join(",", adaptations));
        sb.append("));");
        sb.append("},reject,fulfill);");
        sb.append("});};\n");

        return sb.toString();
    }
}
