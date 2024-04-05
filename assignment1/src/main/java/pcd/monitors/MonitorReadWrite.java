package pcd.monitors;

public interface ReadWriteMonitor {

    void requestWrite();

    void releaseWrite();

    void requestRead();

    void releaseRead();
}
