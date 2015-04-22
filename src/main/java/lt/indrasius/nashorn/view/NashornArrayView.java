package lt.indrasius.nashorn.view;

import jdk.nashorn.api.scripting.AbstractJSObject;

import java.util.Collection;

/**
 * Created by mantas on 15.4.19.
 */
public class NashornArrayView extends AbstractJSObject {
    private Object[] target;

    public NashornArrayView(Object[] target) {
        this.target = target;
    }

    public NashornArrayView(Collection target) {
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

        System.err.println("Member not found: " + name);

        return null;
    }

    @Override
    public boolean hasMember(String name) {
        return name.equals("length");
    }
}
