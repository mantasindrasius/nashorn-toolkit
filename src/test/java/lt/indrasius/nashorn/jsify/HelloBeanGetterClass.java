package lt.indrasius.nashorn.jsify;

/**
 * Created by mantas on 15.4.19.
 */
public class HelloBeanGetterClass {
    private HelloBean[] values;

    public HelloBeanGetterClass(HelloBean... values) {
        this.values = values;
    }

    public HelloBean[] getValues() {
        return values;
    }
}
