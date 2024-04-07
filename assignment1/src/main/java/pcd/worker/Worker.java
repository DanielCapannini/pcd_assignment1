package pcd.worker;

import pcd.engine.AbstractAgent;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread{
    private int dt;
    private final List<AbstractAgent> agents;
    private final CyclicBarrier barrier;

    public Worker(List<AbstractAgent> agents, CyclicBarrier barrier){
        this.agents = agents;
        this.barrier = barrier;
    }

    public void setDt(int dt){
        this.dt=dt;
    }

    @Override
    public void run(){

        for (AbstractAgent agent: agents){
            agent.setDt(dt);
            agent.step();
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }


    }
}
