package pcd.app;

import pcd.view.RoadSimStatistics;
import pcd.view.RoadSimView;
import pcd.example.TrafficSimulationWithCrossRoads;

/**
 * 
 * Main class to create and run a simulation
 * 
 */
public class RunTrafficSimulation {

	public static void main(String[] args) {

		// var simulation = new TrafficSimulationSingleRoadTwoCars();
		// var simulation = new TrafficSimulationSingleRoadSeveralCars();
		// var simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();
		var simulation = new TrafficSimulationWithCrossRoads();
		simulation.setup();

		RoadSimStatistics stat = new RoadSimStatistics();
		RoadSimView view = new RoadSimView(simulation);
		view.display();

		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(view);
		simulation.run();
	}
}
