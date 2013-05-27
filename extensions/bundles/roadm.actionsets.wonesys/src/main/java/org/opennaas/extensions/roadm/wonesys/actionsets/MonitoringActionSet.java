package org.opennaas.extensions.roadm.wonesys.actionsets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.roadm.wonesys.actionsets.actions.alarms.ProcessAlarmAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RefreshModelConnectionsAction;
import org.opennaas.core.resources.action.ActionSet;

public class MonitoringActionSet extends ActionSet {

	public MonitoringActionSet() {
		super.setActionSetId("connectionsActionSet");
		this.putAction(ActionConstants.PROCESSALARM, ProcessAlarmAction.class);
		// this.putAction(ActionConstants.REGISTER, RegisterAsListenerAction.class);
		this.putAction(ActionConstants.REFRESHCONNECTIONS, RefreshModelConnectionsAction.class);

		// this.refreshActions.add(ActionConstants.REGISTER); registering is no longer done in an action
		// loaded model is needed for alarmProcessing
		this.refreshActions.add(ActionConstants.REFRESHCONNECTIONS);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.PROCESSALARM);
		// actionNames.add(ActionConstants.REGISTER);
		actionNames.add(ActionConstants.REFRESHCONNECTIONS);
		return actionNames;
	}

}
