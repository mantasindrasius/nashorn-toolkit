package lt.indrasius.nashorn.jsify;

/**
 * Created by mantas on 15.4.19.
 */
public class SimpleArrayGetterClass<A> {
    private A[] values;

    public SimpleArrayGetterClass(A[] test) {
        this.values = test;
    }

    public A[] getValues() {
        return values;
    }
}
