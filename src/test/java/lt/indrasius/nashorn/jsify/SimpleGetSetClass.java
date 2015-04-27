package lt.indrasius.nashorn.jsify;

/**
 * Created by mantas on 15.4.19.
 */
public class SimpleGetSetClass<A> {
    private A value;

    public void setValue(A value) {
        this.value = value;
    }

    public A getValue() {
        return value;
    }
}
