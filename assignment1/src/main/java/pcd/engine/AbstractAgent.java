package pcd.engine;

/**
 * 
 * Base class for defining types of agents taking part to the simulation
 * 
 */
public abstract class AbstractAgent extends Thread {

	private final String myId;
	protected AbstractEnvironment env;
	protected int dt;

	/**
	 * Each agent has an identifier
	 *
     */
	protected AbstractAgent(String id) {
		this.myId = id;
	}

	/**
	 * This method is called at the beginning of the simulation
	 *
     */
	public void init(AbstractEnvironment env) {
		this.env = env;
	}

	/**
	 * This method is called at each step of the simulation
	 *
     */
	abstract public void step();

	public String getObjectId() {
		return myId;
	}

	protected AbstractEnvironment getEnv() {
		return this.env;
	}

	@Override
	public void run() {
		step();
	}

	public void setDt(int dt) {
		this.dt = dt;
	}

}
