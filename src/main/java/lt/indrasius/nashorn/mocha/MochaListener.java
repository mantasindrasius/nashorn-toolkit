package lt.indrasius.nashorn.mocha;

/**
 * Created by mantas on 15.4.16.
 */
public interface MochaListener {
    void started(int numberOfTests);
    void suiteStarted(String name);
    void testStarted(String name, String description);
    void testIgnored(String name, String description);
    void testPassed(String name, String description, Long duration);
    void testFailed(String name, String description, Long duration, String error, String stack);
    void suiteFinished(String name);
    void finished();
}
