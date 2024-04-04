package engine.util;

public interface Barrier {
    void hitAndWaitAll(int state) throws InterruptedException;
}
