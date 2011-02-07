package net.i2cat.mantychore.core.resources;

public interface ILifecycle {
	/**
	 * Provide a status for the resource instance. 
	 */
	public enum State {INSTANTIATED, INITIALIZED,ACTIVE,INACTIVE,SHUTDOWN,ERROR};
	/**
	 * Returns the current resource state
	 * @return state enum object
	 */
	public State getState();
	
	/**
	 * Sets the current engine state
	 */
	public void setState(State state);
	
	/**
     * Initializes this component, the status will be INITIALIZED,
     * then will be ACTIVE if enabled.   
     *  @throws ResourceException
     */
    public void initialize() throws ResourceException;
    
    /**
     * Activates this component and change state to ACTIVE.
     * @throws ResourceException
     */
    public void activate() throws ResourceException;
    
    /**
     * Deactivate this component and change state to INACTIVE
     * @throws ResourceException
     */
    public void deactivate() throws ResourceException;
    /**
     * Prepares component for Garbage Collection state will be SHUTDOWN
     * until it is collected. 
     * @throws ResourceException
     */
    public void shutdown() throws ResourceException;
}
