package org.opennaas.core.resources.action;

import org.opennaas.core.resources.protocol.IProtocolSessionManager;

import net.i2cat.mantychore.model.ManagedElement;

/**
 * Basic interface all Action must implements
 * 
 * @author Evelyn Torras
 * 
 */
public interface IAction {

	/**
	 * 
	 */
	public ManagedElement getModelToUpdate();

	/**
	 * 
	 */

	public void setModelToUpdate(ManagedElement modelToUpdate);

	/**
	 * 
	 */
	public Object getParams();

	/**
	 * 
	 */
	public void setParams(Object params);

	/**
	 * 
	 */
	public String getActionID();

	/**
	 * 
	 */
	public void setActionID(String actionID);

	/**
	 * 
	 */
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException;

	public boolean checkParams(Object params) throws ActionException;
}
