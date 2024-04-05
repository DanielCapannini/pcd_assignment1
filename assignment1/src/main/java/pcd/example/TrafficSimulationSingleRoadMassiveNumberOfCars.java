package pcd.example;


import pcd.engine.AbstractSimulation;
import pcd.base.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class TrafficSimulationSingleRoadMassiveNumberOfCars extends AbstractSimulation {

	private final int numCars;
	private List<CarAgent> agents;

	public TrafficSimulationSingleRoadMassiveNumberOfCars(int numCars) {
		super();
		this.numCars = numCars;
		this.agents = new ArrayList<>();
	}

	public void setup() {
		this.setupTimings(0, 1);
		CyclicBarrier barrier = new CyclicBarrier(numCars);
		this.setBarrier(barrier);
		RoadsEnv env = new RoadsEnv();
		this.setupEnvironment(env);

		Road road = env.createRoad(new P2d(0, 300), new P2d(15000, 300));

		for (int i = 0; i < numCars; i++) {

			String carId = "car-" + i;
			double initialPos = i * 10;
			double carAcceleration = 1; // + gen.nextDouble()/2;
			double carDeceleration = 0.3; // + gen.nextDouble()/2;
			double carMaxSpeed = 7; // 4 + gen.nextDouble();

			CarAgent car = new CarAgentBasic(carId, env,
					road,
					initialPos,
					carAcceleration,
					carDeceleration,
					carMaxSpeed,
					barrier);
			this.agents.add(car);
			this.addAgent(car);
			/* no sync with wall-time */
		}
	}
}
