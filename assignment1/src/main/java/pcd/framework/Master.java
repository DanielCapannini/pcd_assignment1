package pcd.framework;

public interface Master {
    void submitTask(WorkerTask task);

    void shutdown();
}
