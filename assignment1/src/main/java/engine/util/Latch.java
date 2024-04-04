package engine.util;

public interface Latch {
    void countDown();

    void await() throws InterruptedException;
    void reset();
}
