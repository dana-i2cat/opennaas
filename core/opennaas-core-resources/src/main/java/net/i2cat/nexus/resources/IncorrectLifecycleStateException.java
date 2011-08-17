package net.i2cat.nexus.resources;

import net.i2cat.nexus.resources.ILifecycle.State;

/**
 * Indicates an operation has not been executed because target is not in an appropiated state.
 * 
 * @author isart
 */
public class IncorrectLifecycleStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	State	resourceState;

	public IncorrectLifecycleStateException() {
		super();
	}

	public IncorrectLifecycleStateException(String msg) {
		super(msg);
	}

	public IncorrectLifecycleStateException(String msg, State state) {
		super(msg);
		this.resourceState = state;
	}

	public IncorrectLifecycleStateException(String msg, State state, Throwable cause) {
		super(msg, cause);
		this.resourceState = state;
	}

	public State getResourceState() {
		return resourceState;
	}

	public void setResourceState(State resourceState) {
		this.resourceState = resourceState;
	}
}
