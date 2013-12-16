package org.opennaas.extensions.openflowswitch.driver.floodlight.monitoring.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.openflowswitch.capability.monitoring.MonitoringActionSet;
import org.opennaas.extensions.openflowswitch.driver.floodlight.monitoring.actionssets.actions.GetPortStatisticsAction;

/**
 * Action Set implementation of Monitoring capability
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class MonitoringActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "monitoringActionSetFloodlight";

	public MonitoringActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(MonitoringActionSet.GET_PORT_STATISTICS, GetPortStatisticsAction.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(MonitoringActionSet.GET_PORT_STATISTICS);

		return actionNames;
	}

}
