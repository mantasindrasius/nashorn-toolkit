package lt.indrasius.nashorn.jsify;

import jdk.nashorn.api.scripting.AbstractJSObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by mantas on 15.4.19.
 */
public class ArrayView extends AbstractJSObject {
    private Object[] target;

    private Map<String, Object> dynamicValues = new HashMap<>();

    public ArrayView(Object[] target) {
        this.target = target;
    }

    public ArrayView(Collection target) {
        this.target = target.toArray();
    }

    @Override
    public Object getSlot(int index) {
        try {
            return target[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalAccessError("Out of bound");
        }
    }

    @Override
    public boolean hasSlot(int slot) {
        return slot < target.length;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public Object getMember(String name) {
        if (name.equals("length"))
            return new Long(target.length);

        if (name.equals("getTarget"))
            return (Callable) () -> target;

        return dynamicValues.get(name);
    }

    @Override
    public void setMember(String name, Object value) {
        dynamicValues.put(name, value);
    }

    @Override
    public boolean hasMember(String name) {
        return name.equals("length") || dynamicValues.containsKey(name);
    }
}
