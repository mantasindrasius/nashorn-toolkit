package lt.indrasius.nashorn.jsify;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mantas on 15.5.1.
 */
public class ArgumentAdapter {
    private static Map<String,Class> builtInMap = new HashMap<>();

    static {
        builtInMap.put("int", Integer.class);
        builtInMap.put("long", Long.class);
        builtInMap.put("double", Double.class);
        builtInMap.put("float", Float.class);
        builtInMap.put("bool", Boolean.class);
        builtInMap.put("char", Character.class);
        builtInMap.put("byte", Byte.class);
        builtInMap.put("void", Void.class);
        builtInMap.put("short", Short.class);
    }

    public static Object adapt(Object in, String targetClass) throws ClassNotFoundException {
        Class clz;

        try {
            clz = Class.forName(targetClass);
        } catch (ClassNotFoundException e) {
            clz = builtInMap.get(targetClass);

            if (clz == null) {
                throw e;
            }
        }

        return adapt(in, clz);
    }

    public static Object adapt(Object in, Class targetClass) {
        if (in.getClass() != targetClass)
            return convert(in, targetClass);
        else
            return in;
    }

    private static Object convert(Object in, Class targetClass) {
        if (targetClass == Integer.class)
            return Integer.parseInt(in.toString());
        else if (targetClass == Long.class)
            return Long.parseLong(in.toString());
        else if (targetClass == Float.class)
            return Float.parseFloat(in.toString());
        else if (targetClass == Double.class)
            return Double.parseDouble(in.toString());
        else if (targetClass == Boolean.class)
            return Boolean.parseBoolean(in.toString());

        if (in instanceof ScriptObjectMirror) {
            Object target = null;
            try {
                target = targetClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            ObjectView view = new ObjectView(target);
            ScriptObjectMirror src = (ScriptObjectMirror) in;

            for (String key: src.keySet()) {
                view.setMember(key, src.getMember(key));
            }

            return view.getTarget();
        }

        return in;
    }
}
