package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.GetOFForwardingAction;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class RyuOFForwardingActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "ofForwardingActionSetRyu";

	public RyuOFForwardingActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);

		this.putAction(OpenflowForwardingActionSet.GETFLOWS, GetOFForwardingAction.class);

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
