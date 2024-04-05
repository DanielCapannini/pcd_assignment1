package pcd.base;

import pcd.engine.AbstractAgent;
import pcd.engine.AbstractEnvironment;
import pcd.engine.Action;

import java.util.Optional;

/**
 * 
 * Base class modeling the skeleton of an agent modeling a car in the traffic
 * environment
 * 
 */
public abstract class CarAgent extends AbstractAgent {

	/* car model */
	protected double maxSpeed;
	protected double currentSpeed;
	protected double acceleration;
	protected double deceleration;
	protected CarPercept currentPercept;
	protected Optional<Action> selectedAction;

	public CarAgent(String id, RoadsEnv env, Road road,
			double initialPos,
			double acc,
			double dec,
			double vMax) {
		super(id);
		this.acceleration = acc;
		this.deceleration = dec;
		this.maxSpeed = vMax;
		this.env = env;
		env.registerNewCar(this, road, initialPos);
	}

	/**
	 * 
	 * Basic behaviour of a car agent structured into a sense/decide/act structure
	 * 
	 */
	public void step() {
		AbstractEnvironment env = this.getEnv();
		var readWriteMonitor = env.getMonitorReadWrite();
		readWriteMonitor.requestRead();
		try {
			/* sense */
			currentPercept = (CarPercept) env.getCurrentPercepts(getObjectId());

		} finally {
			readWriteMonitor.releaseRead();
		}

		/* decide */

		selectedAction = Optional.empty();

		decide(dt);

		// /* act */
		readWriteMonitor.requestWrite();
		try {
            selectedAction.ifPresent(action -> env.doAction(getObjectId(), action));
		} finally {
			readWriteMonitor.releaseWrite();
		}
	}

	/**
	 * 
	 * Base method to define the behaviour strategy of the car
	 *
     */
	protected abstract void decide(int dt);

	public double getCurrentSpeed() {
		return currentSpeed;
	}

}
