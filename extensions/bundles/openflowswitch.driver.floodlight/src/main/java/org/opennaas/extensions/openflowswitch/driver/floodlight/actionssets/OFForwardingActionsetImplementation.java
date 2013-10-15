package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions.CreateOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions.GetFlowsActionMockup;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions.RemoveOFForwardingAction;

public class OFForwardingActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "ofForwardingActionSetFloodlight";

	public OFForwardingActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE, CreateOFForwardingAction.class);
		this.putAction(OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE, RemoveOFForwardingAction.class);
		this.putAction(OpenflowForwardingActionSet.GETFLOWS, GetFlowsActionMockup.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE);
		actionNames.add(OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE);
		actionNames.add(OpenflowForwardingActionSet.GETFLOWS);

		return actionNames;
	}

}
