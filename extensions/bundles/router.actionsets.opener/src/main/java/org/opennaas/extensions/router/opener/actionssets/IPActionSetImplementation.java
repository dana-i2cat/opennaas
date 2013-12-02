package org.opennaas.extensions.router.opener.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.capability.ip.IPActionSet;
import org.opennaas.extensions.router.opener.actionssets.actions.GetInterfacesAction;
import org.opennaas.extensions.router.opener.actionssets.actions.SetIPv4Action;

public class IPActionSetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "ipActionSetOPENER";

	public IPActionSetImplementation() {
		super.setActionSetId(ACTIONSET_ID);

		this.putAction(OpenerActionSetConstants.REFRESH_ACTION_OPENER, GetInterfacesAction.class);
		this.putAction(IPActionSet.SET_IPv4, SetIPv4Action.class);

		/* add refresh actions */
		this.refreshActions.add(OpenerActionSetConstants.REFRESH_ACTION_OPENER);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(OpenerActionSetConstants.REFRESH_ACTION_OPENER);
		actionNames.add(IPActionSet.SET_IPv4);

		return actionNames;
	}

}
