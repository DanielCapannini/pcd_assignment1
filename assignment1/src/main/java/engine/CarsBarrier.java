package engine;


import engine.util.Barrier;
import engine.util.Latch;

public class CarsBarrier implements Barrier {
    private final int nCars;
    private final Latch latch;
    private int carsIn1;
    private int carsIn2;
    private int carsIn3;
    public CarsBarrier(int nCars, Latch latch) {
        this.carsIn1 = 0;
        this.carsIn2 = 0;
        this.carsIn3 = 0;
        this.nCars = nCars;
        this.latch = latch;
    }
    @Override
    public void hitAndWaitAll(int state) throws InterruptedException {
        switch (state) {
            case 1:
                hitAndWaitAll1();
                break;
            case 2:
                hitAndWaitAll2();
                break;
            case 3:
                hitAndWaitAll3();
                break;
        }
    }
    private synchronized void hitAndWaitAll1() throws InterruptedException {
        this.carsIn1++;
        if(this.carsIn1 >= this.nCars) {
            notifyAll();
            this.latch.countDown();
            this.carsIn3 = 0;
        } else {
            while(this.carsIn1 < this.nCars) {
                wait();
            }
        }
    }
    private synchronized void hitAndWaitAll2() throws InterruptedException {
        this.carsIn2++;
        if(this.carsIn2 >= this.nCars) {
            notifyAll();
            this.latch.countDown();
            this.carsIn1 = 0;
        } else {
            while(this.carsIn2 < this.nCars) {
                wait();
            }
        }
    }
    private synchronized void hitAndWaitAll3() throws InterruptedException {
        this.carsIn3++;
        if(this.carsIn3 >= this.nCars) {
            notifyAll();
            this.latch.countDown();
            this.carsIn2 = 0;
        } else {
            while(this.carsIn3 < this.nCars) {
                wait();
            }
        }
    }
}
