package lt.indrasius.nashorn.jsify;

/**
 * Created by mantas on 15.4.22.
 */
public class TestService {
    public String blockingMethod() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Hello";
    }

    public String blockingFailMethod() throws Exception {
        blockingMethod();

        throw new Exception("Error");
    }
}
