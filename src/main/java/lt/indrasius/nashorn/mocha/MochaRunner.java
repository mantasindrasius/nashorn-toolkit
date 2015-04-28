package lt.indrasius.nashorn.mocha;

import lt.indrasius.nashorn.exceptions.MochaEngineException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Method;

/**
 * Created by mantas on 15.4.13.
 */
public class MochaRunner extends Runner {
    private Class clazz;
    private Method setupMethod;

    public MochaRunner(Class clazz) {
        this.clazz = clazz;

        for (Method method: this.clazz.getMethods()) {
            if (setupMethod != null) {
                break;
            }

            JSSetup annot = method.getAnnotation(JSSetup.class);
            setupMethod = annot != null ? method : null;
        }
    }

    @Override
    public Description getDescription() {
        return Description.createTestDescription(clazz, clazz.getSimpleName());
    }

    @Override
    public void run(RunNotifier runNotifier) {
        String[] specs = getSpecFiles();
        MochaRuntime runtime = new MochaRuntime();
        MochaJUnitListener listener = new MochaJUnitListener(clazz, runNotifier);

        try {
            Object testSuite = clazz.newInstance();
            runtime.run(specs, listener, engine -> {
                try {
                    if (setupMethod != null)
                        setupMethod.invoke(testSuite, engine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (MochaEngineException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
}

    private String[] getSpecFiles() {
        JSSpec annot = (JSSpec) clazz.getAnnotation(JSSpec.class);

        return annot != null ?  annot.value() : new String[0];
    }
}
