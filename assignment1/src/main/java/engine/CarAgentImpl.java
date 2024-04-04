package engine;


import engine.util.*;

import java.util.Optional;
import java.util.concurrent.CyclicBarrier;

public class CarAgentImpl extends CarAgent {
    private static final int CAR_NEAR_DIST = 15;
    private static final int CAR_FAR_ENOUGH_DIST = 20;
    private static final int MAX_WAITING_TIME = 2;
    private static final int SEM_NEAR_DIST = 100;

    private enum CarAgentState { STOPPED, ACCELERATING,
        DECELERATING_BECAUSE_OF_A_CAR,
        DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM,
        WAITING_FOR_GREEN_SEM,
        WAIT_A_BIT, MOVING_CONSTANT_SPEED}

    private CarAgentState state;

    private long waitingTime;
    private long startingTime;

    public CarAgentImpl(String id, RoadsEnv env, Road road,
                        double initialPos,
                        double acc,
                        double dec,
                        double vmax,
                        Barrier barrier, Counter counter,
                        CyclicBarrier simulationBarrier) {
        super(id, env, road, initialPos, acc, dec, vmax,
                barrier, counter, simulationBarrier);
        state = CarAgentState.STOPPED;
        this.startingTime = System.currentTimeMillis();
    }

    @Override
    public synchronized void decide() {
        double timePassed = (System.currentTimeMillis() - startingTime) / 100.0;
        switch (state) {
            case CarAgentState.STOPPED:
                if (!detectedNearCar()) {
                    state = CarAgentState.ACCELERATING;
                }
                break;
            case CarAgentState.ACCELERATING:
                if (detectedNearCar()) {
                    state = CarAgentState.DECELERATING_BECAUSE_OF_A_CAR;
                } else if (detectedRedOrOrgangeSemNear()) {
                    state = CarAgentState.DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM;
                } else {
                    this.currentSpeed += acceleration * timePassed;
                    if (currentSpeed >= maxSpeed) {
                        state = CarAgentState.MOVING_CONSTANT_SPEED;
                    }
                }
                break;
            case CarAgentState.MOVING_CONSTANT_SPEED:
                if (detectedNearCar()) {
                    state = CarAgentState.DECELERATING_BECAUSE_OF_A_CAR;
                } else if (detectedRedOrOrgangeSemNear()) {
                    state = CarAgentState.DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM;
                }
                break;
            case CarAgentState.DECELERATING_BECAUSE_OF_A_CAR:
                this.currentSpeed -= deceleration * timePassed;
                if (this.currentSpeed <= 0) {
                    state =  CarAgentState.STOPPED;
                } else if (this.carFarEnough()) {
                    state = CarAgentState.WAIT_A_BIT;
                    waitingTime = System.currentTimeMillis();
                }
                break;
            case CarAgentState.DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM:
                this.currentSpeed -= deceleration * timePassed;
                if (this.currentSpeed <= 0) {
                    state =  CarAgentState.WAITING_FOR_GREEN_SEM;
                } else if (!detectedRedOrOrgangeSemNear()) {
                    state = CarAgentState.ACCELERATING;
                }
                break;
            case CarAgentState.WAIT_A_BIT:
                long waitingTimePassed = (int)(System.currentTimeMillis() - waitingTime) / 1000;
                if (waitingTimePassed > MAX_WAITING_TIME) {
                    state = CarAgentState.ACCELERATING;
                }
                break;
            case CarAgentState.WAITING_FOR_GREEN_SEM:
                if (detectedGreenSem()) {
                    state = CarAgentState.ACCELERATING;
                }
                break;
        }

        if (currentSpeed > 0) {
            selectedAction = Optional.of(new MoveForward(currentSpeed * timePassed));
        } else {
            currentSpeed = 0;
        }
        startingTime = System.currentTimeMillis();
    }

    private boolean detectedNearCar() {
        Optional<CarAgentInfo> car = currentPercept.nearestCarInFront();
        if (car.isEmpty()) {
            return false;
        } else {
            double dist = car.get().getPos() - currentPercept.roadPos();
            return dist < CAR_NEAR_DIST;
        }
    }

    private boolean detectedRedOrOrgangeSemNear() {
        Optional<TrafficLightInfo> sem = currentPercept.nearestSem();
        if (sem.isEmpty() || sem.get().sem().isGreen()) {
            return false;
        } else {
            double dist = sem.get().roadPos() - currentPercept.roadPos();
            return dist > 0 && dist < SEM_NEAR_DIST;
        }
    }

    private boolean detectedGreenSem() {
        Optional<TrafficLightInfo> sem = currentPercept.nearestSem();
        return (sem.isPresent() && sem.get().sem().isGreen());
    }

    private boolean carFarEnough() {
        Optional<CarAgentInfo> car = currentPercept.nearestCarInFront();
        if (car.isEmpty()) {
            return true;
        } else {
            double dist = car.get().getPos() - currentPercept.roadPos();
            return dist > CAR_FAR_ENOUGH_DIST;
        }
    }
}
