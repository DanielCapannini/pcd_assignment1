package base;

/**
 *
 * Base  class for defining types of agents taking part to the simulation
 *
 */
public abstract class AbstractAgent extends Thread {
    private AbstractEnvironment env;

    /**
     * Each agent has an identifier
     *
     * @param id
     */
    protected AbstractAgent(String id) {
        super(id);
    }

    /**
     * This method is called at the beginning of the simulation
     *
     * @param env
     */
    public void init(AbstractEnvironment env) {
        this.env = env;
    }

    protected AbstractEnvironment getEnv() {
        return this.env;
    }
}

