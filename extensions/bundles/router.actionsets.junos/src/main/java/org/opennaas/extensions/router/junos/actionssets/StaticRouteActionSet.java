/**
 * 
 */
package org.opennaas.extensions.router.junos.actionssets;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.staticroute.CreateStaticRouteAction;
import org.opennaas.extensions.router.junos.actionssets.actions.staticroute.DeleteStaticRouteAction;

/**
 * @author Jordi
 */
public class StaticRouteActionSet extends ActionSet {

	public StaticRouteActionSet() {
		super.setActionSetId("staticRouteActionSet");

		this.putAction(ActionConstants.STATIC_ROUTE_CREATE, CreateStaticRouteAction.class);
		this.putAction(ActionConstants.STATIC_ROUTE_DELETE, DeleteStaticRouteAction.class);
	}
}
