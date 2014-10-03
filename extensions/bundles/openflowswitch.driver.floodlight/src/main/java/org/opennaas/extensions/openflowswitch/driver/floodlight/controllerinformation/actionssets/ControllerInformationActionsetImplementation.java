package org.opennaas.extensions.openflowswitch.driver.floodlight.controllerinformation.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.ControllerInformationActionSet;
import org.opennaas.extensions.openflowswitch.driver.floodlight.controllerinformation.actions.GetHealthStateAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.controllerinformation.actions.GetMemoryUsageAction;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ControllerInformationActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "controllerInformationActionsetFloodlight";

	public ControllerInformationActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(ControllerInformationActionSet.GET_HEALTH_STATE, GetHealthStateAction.class);
		this.putAction(ControllerInformationActionSet.GET_MEMORY_USAGE, GetMemoryUsageAction.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(ControllerInformationActionSet.GET_HEALTH_STATE);
		actionNames.add(ControllerInformationActionSet.GET_MEMORY_USAGE);

		return actionNames;
	}

}
