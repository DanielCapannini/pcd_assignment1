package pcd.base;

import pcd.engine.AbstractAgent;
import pcd.engine.AbstractEnvironment;
import pcd.engine.Action;

import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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

	/* percept and action retrieved and submitted at each step */
	protected CarPercept currentPercept;
	protected Optional<Action> selectedAction;

	private CyclicBarrier barrier;

	public CarAgent(String id, RoadsEnv env, Road road,
			double initialPos,
			double acc,
			double dec,
			double vmax,
			CyclicBarrier barrier) {
		super(id);
		this.acceleration = acc;
		this.deceleration = dec;
		this.maxSpeed = vmax;
		this.env = env;
		this.barrier = barrier;
		env.registerNewCar(this, road, initialPos);
	}

	/**
	 * 
	 * Basic behaviour of a car agent structured into a sense/decide/act structure
	 * 
	 */
	public void step() {
		AbstractEnvironment env = this.getEnv();
		var readWriteMonitor = env.getReadWriteMonitor();
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
		try {
			this.barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			throw new RuntimeException(e);
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

	protected void log(String msg) {
		System.out.println("[CAR " + this.getObjectId() + "] " + msg);
	}
}
