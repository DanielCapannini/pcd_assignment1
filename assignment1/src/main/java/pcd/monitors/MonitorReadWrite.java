package pcd.monitors;

public interface MonitorReadWrite {

    void requestWrite();

    void releaseWrite();

    void requestRead();

    void releaseRead();
}
