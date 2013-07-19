package org.opennaas.extensions.router.opener.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.capability.ip.IPActionSet;
import org.opennaas.extensions.router.opener.actionssets.actions.DummyAction;

public class IPActionSetImplementation extends ActionSet {
	
	public static final String ACTIONSET_ID = "ipActionSetOPENER";
	public static final String REFRESH_ACTION_OPENER = "openerRefreshAction";
	
	public IPActionSetImplementation(){
		super.setActionSetId(ACTIONSET_ID);
		
		this.putAction(REFRESH_ACTION_OPENER, DummyAction.class);
		this.putAction(IPActionSet.SET_IPv4, DummyAction.class);
		
		/* add refresh actions */
		this.refreshActions.add(REFRESH_ACTION_OPENER);
	}
	
	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(REFRESH_ACTION_OPENER);
		actionNames.add(IPActionSet.SET_IPv4);
		
		return actionNames;
	}

}
