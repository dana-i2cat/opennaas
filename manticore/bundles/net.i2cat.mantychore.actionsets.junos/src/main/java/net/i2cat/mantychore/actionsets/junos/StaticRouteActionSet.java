/**
 * 
 */
package net.i2cat.mantychore.actionsets.junos;

import net.i2cat.mantychore.actionsets.junos.actions.staticroute.CreateStaticRouteAction;

import org.opennaas.core.resources.action.ActionSet;

/**
 * @author Jordi
 */
public class StaticRouteActionSet extends ActionSet {

	public StaticRouteActionSet() {
		super.setActionSetId("staticRouteActionSet");

		this.putAction(ActionConstants.STATIC_ROUTE_CREATE, CreateStaticRouteAction.class);
	}
}
