package lt.indrasius.nashorn;

import lt.indrasius.nashorn.exceptions.MochaEngineException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * Created by mantas on 15.4.13.
 */
public class MochaRunner extends Runner {
    private Class clazz;

    public MochaRunner(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public void run(RunNotifier runNotifier) {
        String[] specs = getSpecFiles();
        MochaRuntime runtime = new MochaRuntime();
        MochaJUnitListener listener = new MochaJUnitListener(clazz, runNotifier);

        try {
            runtime.run(specs, listener);
        } catch (MochaEngineException e) {
            e.printStackTrace();
        }
    }

    private String[] getSpecFiles() {
        JSSpec annot = (JSSpec) clazz.getAnnotation(JSSpec.class);

        return annot != null ?  annot.value() : new String[0];
    }
}
