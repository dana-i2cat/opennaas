package org.opennaas.core.resources;

public interface ILifecycle {
	/**
	 * Provide a status for the resource instance.
	 */
	public enum State {
		INSTANTIATED, INITIALIZED, ACTIVE, INACTIVE, SHUTDOWN, ERROR
	};

	/**
	 * Returns the current resource state
	 *
	 * @return state enum object
	 */
	public State getState();

	/**
	 * Sets the current engine state
	 */
	public void setState(State state);

	/**
	 * Initializes this component, the status will be INITIALIZED, then will be ACTIVE if enabled.
	 *
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during initialization
	 */
	public void initialize() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;

	/**
	 * Activates this component and change state to ACTIVE.
	 *
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during activation
	 */
	public void activate() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;

	/**
	 * Deactivate this component and change state to INACTIVE
	 *
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during deactivation
	 */
	public void deactivate() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;

	/**
	 * Prepares component for Garbage Collection state will be SHUTDOWN until it is collected.
	 *
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during shutting down
	 */
	public void shutdown() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;
}
