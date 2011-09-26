package net.i2cat.nexus.resources.action;

import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;

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

	/**
	 * What it is the utility for this method
	 * 
	 * @param params
	 * @return
	 * @throws ActionException
	 */
	public boolean checkParams(Object params) throws ActionException;
}
