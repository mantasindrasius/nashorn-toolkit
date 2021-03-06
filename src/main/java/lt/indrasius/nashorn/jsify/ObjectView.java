package lt.indrasius.nashorn.jsify;

import jdk.nashorn.api.scripting.AbstractJSObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by mantas on 15.4.19.
 */
public class ObjectView extends AbstractJSObject {
    private Object target;
    private Class targetClass;

    private Map<String, Method> getters = new HashMap<>();
    private Map<String, Method> setters = new HashMap<>();
    private Map<String, Object> dynamicValues = new HashMap<>();
    private Set<String> fields = new HashSet<>();

    private static Set<Class<?>> unwrappedClasses = new HashSet<>();

    static {
        unwrappedClasses.add(Boolean.class);
        unwrappedClasses.add(Character.class);
        unwrappedClasses.add(Byte.class);
        unwrappedClasses.add(Short.class);
        unwrappedClasses.add(Integer.class);
        unwrappedClasses.add(Long.class);
        unwrappedClasses.add(Float.class);
        unwrappedClasses.add(Double.class);
        unwrappedClasses.add(Void.class);
        unwrappedClasses.add(String.class);
    }

    public static Object wrap(Object target) {
        if (target == null)
            return null;

        return !unwrappedClasses.contains(target.getClass()) ?
            new ObjectView(target) :
            target;
    }

    public ObjectView(Object target) {
        this.target = target;
        this.targetClass = target.getClass();

        Method[] methods = targetClass.getMethods();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            String fieldName = resolveFieldName(name);

            if (name.startsWith("set"))
                setters.put(fieldName, method);

            if (name.startsWith("get"))
                getters.put(fieldName, method);

            if (fieldName != null)
                fields.add(fieldName);
        }
    }

    public Object getTarget() {
        return target;
    }

    public boolean hasMember(String name) {
        return getGetter(name) != null || dynamicValues.containsKey(name);
    }

    @Override
    public Object getMember(String name) {
        if (name.equals("getTarget")) {
            return (Callable) () -> getTarget();
        }

        Method getter = getGetter(name);

        try {
            if (getter != null) {
                return resolveValue(getter.invoke(target));
            } else {
                return dynamicValues.get(name);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setMember(String name, Object value) {
        Method setter = getSetter(name);

        if (setter == null) {
            if (getGetter(name) != null)
                throw new IllegalAccessError("Object has no " + name + " setter");
            else {
                dynamicValues.put(name, value);
                return;
            }
        }

        try {
            setter.invoke(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean isMemberArray(String name) {
        Class returnType = getGetter(name).getReturnType();

        return returnType.isArray();
    }

    public Class getArrayMemberType(String name) {
        Class returnType = getGetter(name).getReturnType();

        return returnType.getComponentType();
    }

    @Override
    public Set<String> keySet() {
        return fields;
    }

    private Object resolveValue(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj.getClass().isPrimitive() || unwrappedClasses.contains(obj.getClass())) {
            return obj;
        } else if (obj.getClass().isArray()) {
            return new ArrayView((Object[]) obj);
        } else if (obj instanceof Collection) {
            return new ArrayView((Collection)obj);
        } else {
            return new ObjectView(obj);
        }
    }

    private String resolveFieldName(String accessorName) {
        if (!accessorName.startsWith("get") && !accessorName.startsWith("set")) {
            return null;
        }

        if (accessorName.equals("getClass")) {
            return null;
        }

        String trimmed = accessorName.substring(3);

        return trimmed.substring(0, 1).toLowerCase() + trimmed.substring(1);
    }

    private Method getSetter(String name) {
        return setters.get(name);
    }

    private Method getGetter(String name) {
        return getters.get(name);
    }
}
