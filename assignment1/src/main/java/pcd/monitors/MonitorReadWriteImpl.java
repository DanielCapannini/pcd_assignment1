package pcd.monitors;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorReadWriteImpl implements MonitorReadWrite {

    private int numReader;
    private int numWriter;
    private final Lock mutex;
    private final Condition okRead;
    private final Condition okWrite;

    public MonitorReadWriteImpl() {
        this.numReader = 0;
        this.numWriter = 0;
        this.mutex = new ReentrantLock();
        this.okRead = mutex.newCondition();
        this.okWrite = mutex.newCondition();
    }

    @Override
    public void requestWrite() {
        mutex.lock();
        try {
            while (numWriter > 0 || numReader > 0) {
                try {
                    okWrite.await();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            this.numWriter++;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ciao");
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void releaseWrite() {
        mutex.lock();
        try {
            this.numWriter--;
            okWrite.signal();
            okRead.signalAll();
        } catch (Exception e) {
            Thread.currentThread().interrupt();

        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void requestRead() {
        mutex.lock();
        try {
            while (numWriter > 0) {
                try {
                    okRead.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("ciao");
                }
            }
            this.numReader++;
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void releaseRead() {
        mutex.lock();
        try {
            this.numReader--;
            if (this.numReader == 0) {
                okWrite.signal();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.unlock();
        }
    }
}
