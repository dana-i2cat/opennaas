package org.opennaas.extensions.router.opener.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.capability.chassis.ChassisActionSet;
import org.opennaas.extensions.router.opener.actionssets.actions.DummyAction;

public class ChassisActionSetImplementation extends ActionSet {
	
	public static final String ACTIONSET_ID = "chassisActionSetOPENER";
	
	public ChassisActionSetImplementation(){
		super.setActionSetId(ACTIONSET_ID);
		
		this.putAction(OpenerActionSetConstants.REFRESH_ACTION_OPENER, DummyAction.class);
		this.putAction(ChassisActionSet.CONFIGURESUBINTERFACE, DummyAction.class);
		this.putAction(ChassisActionSet.DELETESUBINTERFACE, DummyAction.class);
		
		/* add refresh actions */
		this.refreshActions.add(OpenerActionSetConstants.REFRESH_ACTION_OPENER);
	}
	
	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(OpenerActionSetConstants.REFRESH_ACTION_OPENER);
		actionNames.add(ChassisActionSet.CONFIGURESUBINTERFACE);
		actionNames.add(ChassisActionSet.DELETESUBINTERFACE);
		
		return actionNames;
	}

}
