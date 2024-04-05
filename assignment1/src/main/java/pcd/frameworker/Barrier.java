package pcd.frameworker;

public interface Barrier {

    void hitAndWaitAll() throws InterruptedException;

}