package pcd.master;


import java.util.concurrent.BlockingQueue;

public class Worker implements Runnable {
    private final BlockingQueue<Task> tasksQueue;

    public Worker(BlockingQueue<Task> tasksQueue) {
        this.tasksQueue = tasksQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = tasksQueue.take();
                task.execute();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
