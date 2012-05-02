package org.opennaas.core.resources.capability;

import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;

public interface IQueueingCapability extends ICapability {

	/**
	 * Creates an action given its id.
	 * 
	 * @param actionId
	 * @return
	 * @throws CapabilityException
	 */
	public IAction createAction(String actionId) throws CapabilityException;

	/**
	 * Adds given action at the end of the queue.
	 * 
	 * @param action
	 *            to put in the queue
	 * @throws CapabilityException
	 *             if failed to put action in the queue
	 */
	public void queueAction(IAction action) throws CapabilityException;

	/**
	 * Sends to the queue all actions required to refresh this the model required for this capability.
	 * 
	 * @throws CapabilityException
	 */
	public void sendRefreshActions() throws CapabilityException;

	/**
	 * Returns the list of actions ids that are available for this capability.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public IActionSet getActionSet() throws CapabilityException;

}
