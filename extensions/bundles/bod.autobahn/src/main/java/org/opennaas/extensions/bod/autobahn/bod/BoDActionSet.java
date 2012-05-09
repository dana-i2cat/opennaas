package org.opennaas.extensions.bod.autobahn.bod;

import org.opennaas.core.resources.action.ActionSet;

public class BoDActionSet extends ActionSet
{
	public BoDActionSet()
	{
		setActionSetId("bodActionSet");

		putAction(RequestConnectionAction.ACTIONID,
				  RequestConnectionAction.class);
		putAction(ShutdownConnectionAction.ACTIONID,
				  ShutdownConnectionAction.class);
		putAction(GetTopologyAction.ACTIONID,
				  GetTopologyAction.class);

		refreshActions.add(GetTopologyAction.ACTIONID);
	}
}
