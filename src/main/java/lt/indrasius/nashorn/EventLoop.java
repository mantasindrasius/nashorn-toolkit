package lt.indrasius.nashorn;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by mantas on 15.4.27.
 */
public class EventLoop {
    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    private final Timer loop = new Timer("jsEventLoop", false);
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    private class TasksHandler extends TimerTask {
        @Override
        public void run() {
            Runnable task;

            while ((task = taskQueue.poll()) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public EventLoop() {
        loop.scheduleAtFixedRate(new TasksHandler(), 1, 5);
    }

    public void unblock(Callable action, Consumer<Throwable> reject, Consumer<Object> fulfill) {
        pool.submit(() -> execTask(action, reject, fulfill));
    }

    public void schedule(Runnable action, int timeout) {
        loop.schedule(new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        }, timeout);
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
        taskQueue.add(f);
    }
}
