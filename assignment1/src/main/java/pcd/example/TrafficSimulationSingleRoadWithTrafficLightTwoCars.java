package pcd.example;

import pcd.engine.AbstractSimulation;
import pcd.base.*;

import java.util.concurrent.CyclicBarrier;

/**
 * 
 * Traffic Simulation about 2 cars moving on a single road, with one semaphore
 * 
 */
public class TrafficSimulationSingleRoadWithTrafficLightTwoCars extends AbstractSimulation {

	public TrafficSimulationSingleRoadWithTrafficLightTwoCars() {
		super();
	}

	public void setup() {

		this.setupTimings(0, 1);
		CyclicBarrier barrier = new CyclicBarrier(2);
		this.setBarrier(barrier);
		RoadsEnv env = new RoadsEnv();
		this.setupEnvironment(env);

		Road r = env.createRoad(new P2d(0, 300), new P2d(1500, 300));

		TrafficLight tl = env.createTrafficLight(new P2d(740, 300), TrafficLight.TrafficLightState.GREEN, 75, 25, 100);
		r.addTrafficLight(tl, 740);

		CarAgent car1 = new CarAgentExtended("car-1", env, r, 0, 0.1, 0.3, 6, barrier);
		this.addAgent(car1);
		CarAgent car2 = new CarAgentExtended("car-2", env, r, 100, 0.1, 0.3, 5, barrier);
		this.addAgent(car2);

		this.syncWithTime();
	}

}
