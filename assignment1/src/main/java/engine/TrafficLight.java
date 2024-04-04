package engine;

import engine.util.P2d;

public class TrafficLight {
    public static enum TrafficLightState {GREEN, YELLOW, RED}
    private TrafficLightState state, initialState;
    private long currentTimeInState;
    private long startingTime;
    private int redDuration, greenDuration, yellowDuration;
    private P2d pos;

    public TrafficLight(P2d pos, TrafficLightState initialState, int greenDuration, int yellowDuration, int redDuration) {
        this.redDuration = redDuration * 100;
        this.greenDuration = greenDuration * 100;
        this.yellowDuration = yellowDuration * 100;
        this.pos = pos;
        this.initialState = initialState;
    }
    public void init() {
        state = initialState;
        currentTimeInState = 0;
        startingTime = System.currentTimeMillis();
    }

    public synchronized void step(long timePassed) {
        long time = timePassed - startingTime;
        currentTimeInState += time;
        switch (state) {
            case TrafficLightState.GREEN:
                if (currentTimeInState >= greenDuration) {
                    state = TrafficLightState.YELLOW;
                    currentTimeInState = 0;
                    startingTime = System.currentTimeMillis();
                }
                break;
            case TrafficLightState.RED:
                if (currentTimeInState >= redDuration) {
                    state = TrafficLightState.GREEN;
                    currentTimeInState = 0;
                    startingTime = System.currentTimeMillis();
                }
                break;
            case TrafficLightState.YELLOW:
                if (currentTimeInState >= yellowDuration) {
                    state = TrafficLightState.RED;
                    currentTimeInState = 0;
                    startingTime = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }

    public boolean isGreen() {
        return state.equals(TrafficLightState.GREEN);
    }

    public boolean isRed() {
        return state.equals(TrafficLightState.RED);
    }

    public boolean isYellow() {
        return state.equals(TrafficLightState.YELLOW);
    }

    public P2d getPos() {
        return pos;
    }
}
