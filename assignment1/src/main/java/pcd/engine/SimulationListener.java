package pcd.engine;

import java.util.List;

public interface SimulationListener {

	/**
	 * Called at the beginning of the simulation
	 *
	 */
	void notifyInit(int t, List<AbstractAgent> agents, AbstractEnvironment env);

	/**
	 * Called at each step, updater all updates
	 *
	 */
	void notifyStepDone(int t, List<AbstractAgent> agents, AbstractEnvironment env);
}
