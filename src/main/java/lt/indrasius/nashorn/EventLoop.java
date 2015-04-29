package lt.indrasius.nashorn;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by mantas on 15.4.27.
 */
public class EventLoop {
    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    private final Timer loop = new Timer("jsEventLoop", false);

    public void unblock(Callable action, Consumer<Throwable> reject, Consumer<Object> fulfill) {
        pool.submit(() -> execTask(action, reject, fulfill));
    }

    public TimerTask schedule(Runnable action, int timeout) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        };

        loop.schedule(task, timeout);

        return task;
    }

    public TimerTask repeat(Runnable action, int interval) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        };

        loop.scheduleAtFixedRate(task, interval, interval);

        return task;
    }

    private void execTask(Callable action, Consumer<Throwable> reject, Consumer<Object> fulfill) {
        try {
            Object result = action.call();

            enqueue(() -> fulfill.accept(result));
        } catch (Throwable e) {
            enqueue(() -> reject.accept(e));
        }
    }

    private void enqueue(Runnable f) {
        schedule(f, 0);
    }
}
