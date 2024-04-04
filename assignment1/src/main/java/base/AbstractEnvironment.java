package base;


import engine.util.Action;
import engine.util.Percept;

/**
 *
 * Base class to define the environment of the simulation
 *
 */
public abstract class AbstractEnvironment {
    private String id;
    protected AbstractEnvironment(String id) {
        this.id = id;
    }

    public String getName() {
        return id;
    }

    /**
     *
     * Called at the beginning of the simulation
     */
    public abstract void init();

    /**
     *
     * Called at each step of the simulation
     *
     * @param timePassed
     */
    public abstract void step(long timePassed);

    /**
     *
     * Called by an agent to get its percepts
     *
     * @param agentId - identifier of the agent
     * @return agent percept
     */
    public abstract Percept getCurrentPercepts(String agentId);

    /**
     *
     * Called by agent to submit an action to the environment
     *
     * @param agentId - identifier of the agent doing the action
     * @param act - the action
     */
    public abstract void doAction(String agentId, Action act);
}
