package engine;


import base.AbstractAgent;
import base.AbstractEnvironment;
import engine.util.*;

import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class CarAgent extends AbstractAgent {
    private final Counter counter;
    private final Barrier carsBarrier;
    private final CyclicBarrier simulationBarrier;
    private final AbstractEnvironment roadEnv;
    /* car model */
    protected double maxSpeed;
    protected double currentSpeed;
    protected double acceleration;
    protected double deceleration;

    /* percept and action retrieved and submitted at each step */
    protected CarPercept currentPercept;
    protected Optional<Action> selectedAction;


    public CarAgent(String id, RoadsEnv env, Road road,
                    double initialPos, double acc,
                    double dec, double vmax,
                    Barrier barrier, Counter counter,
                    CyclicBarrier simulationBarrier
    ) {
        super(id);
        this.acceleration = acc;
        this.deceleration = dec;
        this.maxSpeed = vmax;
        this.carsBarrier = barrier;
        this.simulationBarrier = simulationBarrier;
        this.roadEnv = env;
        this.counter = counter;
        env.registerNewCar(this, road, initialPos);
    }

    @Override
    public void run() {
        while (counter.isNotMax()) {
            try {
                /* sense */
                currentPercept = (CarPercept) roadEnv.getCurrentPercepts(getName());
                carsBarrier.hitAndWaitAll(1);

                /* decide */
                selectedAction = Optional.empty();
                decide();
                carsBarrier.hitAndWaitAll(2);

                /* act */
                selectedAction.ifPresent(action -> roadEnv.doAction(getName(), action));
                carsBarrier.hitAndWaitAll(3);
                simulationBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract void decide();

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    protected void log(String msg) {
        System.out.println("[CAR " + getName() + "] " + msg);
    }
}
