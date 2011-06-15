package net.i2cat.nexus.resources.action;

import java.util.List;

public interface IActionSet {

	public String getActionSetId();

	public void setActionSetId(String actionSetId);

	public List<String> getActionNames();

	/**
	 * 
	 * @param actionId
	 * @return Action with given actionId present in this actionSet, or null if there is no action with given Id
	 * @throws ActionException
	 *             if there was a problem instantiating the action
	 */
	public Action obtainAction(String actionId) throws ActionException;
}
