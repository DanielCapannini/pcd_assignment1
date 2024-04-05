package pcd.master;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MasterImpl implements Master {

    private final BlockingQueue<Task> taskQueue;
    private final List<Thread> workers;

    public MasterImpl(int numWorkers) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new ArrayList<>();
        for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker(taskQueue);
            Thread threadWorker = new Thread(worker);
            workers.add(threadWorker);
            threadWorker.start();
        }
    }

    @Override
    public void addTask(Task task) {
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void shutdownTask() {
        for (Thread worker : workers) {
            worker.interrupt();
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
