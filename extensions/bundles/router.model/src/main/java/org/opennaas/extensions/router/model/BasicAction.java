package org.opennaas.extensions.router.model;

public class BasicAction extends PolicyAction {

	/**
	 * The following constants are defined for use with the ValueMap/Values qualified property Action.
	 */

	public enum Action {
		PERMIT,
		DENY
	}

	private Action	action;

	/**
	 * This method returns the BGPRouteMap.action property value. This property is described as follows:
	 * 
	 * This defines whether the action should be to forward or deny traffic meeting the match condition specified in this RouteMap.
	 * 
	 * @return int current action property value
	 * @exception Exception
	 */
	public Action getAction() {

		return this.action;
	} // getAction

	/**
	 * This method sets the BGPRouteMap.action property value. This property is described as follows:
	 * 
	 * This defines whether the action should be to forward or deny traffic meeting the match condition specified in this RouteMap.
	 * 
	 * @param int new action property value
	 * @exception Exception
	 */
	public void setAction(Action action) {

		this.action = action;
	} // setAction

}
