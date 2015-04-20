package lt.indrasius.nashorn.view;

/**
 * Created by mantas on 15.4.19.
 */
public class SimpleGetterClass<A> {
    private A value;

    public SimpleGetterClass(A test) {
        this.value = test;
    }

    public A getValue() {
        return value;
    }
}
