package lt.indrasius.nashorn;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/**
 * Created by mantas on 15.4.16.
 */
public class MochaJUnitListener implements MochaListener {
    private RunNotifier notifier;
    private Class clazz;

    public MochaJUnitListener(Class clazz,  RunNotifier notifier) {
        this.clazz = clazz;
        this.notifier = notifier;
    }

    @Override
    public void suiteStarted(String name) {

    }

    @Override
    public void suiteFinished(String name) {

    }

    @Override
    public void started(int numberOfTests) {

    }

    @Override
    public void finished() {

    }

    @Override
    public void testStarted(String name, String description) {
        notifier.fireTestStarted(descriptionFor(name));
    }

    @Override
    public void testIgnored(String name, String description) {

    }

    @Override
    public void testPassed(String name, String description, Long duration) {

    }

    @Override
    public void testFailed(String name, String description, Long duration, String error) {

    }

    private Description descriptionFor(String name) {
        return Description.createTestDescription(clazz, name);
    }
}
