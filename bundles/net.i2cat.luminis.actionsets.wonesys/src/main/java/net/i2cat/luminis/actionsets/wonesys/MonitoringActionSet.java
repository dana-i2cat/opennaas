package net.i2cat.luminis.actionsets.wonesys;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.luminis.actionsets.wonesys.actions.ProcessAlarmAction;
import net.i2cat.luminis.actionsets.wonesys.actions.RefreshModelConnectionsAction;
import net.i2cat.luminis.actionsets.wonesys.actions.RegisterAsListenerAction;
import org.opennaas.core.resources.action.ActionSet;

public class MonitoringActionSet extends ActionSet {

	public MonitoringActionSet() {
		super.setActionSetId("connectionsActionSet");
		this.putAction(ActionConstants.PROCESSALARM, ProcessAlarmAction.class);
		this.putAction(ActionConstants.REGISTER, RegisterAsListenerAction.class);
		this.putAction(ActionConstants.REFRESHCONNECTIONS, RefreshModelConnectionsAction.class);

		this.refreshActions.add(ActionConstants.REGISTER);
		// loaded model is needed for alarmProcessing
		this.refreshActions.add(ActionConstants.REFRESHCONNECTIONS);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.PROCESSALARM);
		actionNames.add(ActionConstants.REGISTER);
		actionNames.add(ActionConstants.REFRESHCONNECTIONS);
		return actionNames;
	}

}
