package pcd.monitors;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteMonitorImpl implements ReadWriteMonitor {

    private int readers;
    private int writers;
    private final Lock mutex;
    private final Condition okToRead;
    private final Condition okToWrite;

    public ReadWriteMonitorImpl() {
        this.readers = 0;
        this.writers = 0;
        this.mutex = new ReentrantLock();
        this.okToRead = mutex.newCondition();
        this.okToWrite = mutex.newCondition();
    }

    @Override
    public void requestRead() {
        mutex.lock();
        try {
            while (writers > 0) {
                try {
                    okToRead.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            this.readers++;
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void releaseRead() {
        mutex.lock();
        try {
            this.readers--;
            if (this.readers == 0) {
                okToWrite.signal();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void requestWrite() {
        mutex.lock();
        try {
            while (writers > 0 || readers > 0) {
                try {
                    okToWrite.await();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            this.writers++;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return;
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void releaseWrite() {
        mutex.lock();
        try {
            this.writers--;
            okToWrite.signal();
            okToRead.signalAll();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return;
        } finally {
            mutex.unlock();
        }
    }

}
