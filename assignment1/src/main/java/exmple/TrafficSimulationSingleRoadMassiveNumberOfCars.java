package exmple;


import base.AbstractSimulation;
import engine.*;
import engine.util.*;

import java.util.concurrent.CyclicBarrier;

public class TrafficSimulationSingleRoadMassiveNumberOfCars extends AbstractSimulation {

	private int numCars;
	private int nSteps;
	
	public TrafficSimulationSingleRoadMassiveNumberOfCars(int numCars, int nSteps) {
		super();
		this.numCars = numCars;
		this.nSteps = nSteps;
	}
	
	public void setup() {
		Latch roadsLatch = new RoadLatch(3);
		CyclicBarrier simulationBarrier = new CyclicBarrier(numCars+1);
		Barrier barrier = new CarsBarrier(numCars, roadsLatch);
		Counter counter = new Counter(nSteps);
		this.setup(simulationBarrier, roadsLatch, counter);

		RoadsEnv env = new RoadsEnv();
		this.setupEnvironment(env);
		
		Road road = env.createRoad(new P2d(0,300), new P2d(15000,300));

		for (int i = 0; i < numCars; i++) {
			
			String carId = "car-" + i;
			double initialPos = i*10;			
			double carAcceleration = 1; //  + gen.nextDouble()/2;
			double carDeceleration = 0.3; //  + gen.nextDouble()/2;
			double carMaxSpeed = 7; // 4 + gen.nextDouble();
						
			CarAgent car = new CarAgentImpl(carId, env,
									road,
									initialPos, 
									carAcceleration, 
									carDeceleration,
									carMaxSpeed, barrier, counter, simulationBarrier);
			this.addAgent(car);
			
			/* no sync with wall-time */
		}
		
	}	
}
	