package engine;


import engine.util.Latch;

public class RoadLatch implements Latch {
    private int counter;
    private final int states;
    public RoadLatch(int states) {
        this.states = states;
        this.counter = states;
    }

    public synchronized void countDown() {
        this.counter--;
        if(this.counter == 0) {
            notifyAll();
        }
    }

    public synchronized void await() throws InterruptedException {
        while(this.counter > 0) {
            wait();
        }
    }

    public synchronized void reset() {
        this.counter = states;
    }
}
