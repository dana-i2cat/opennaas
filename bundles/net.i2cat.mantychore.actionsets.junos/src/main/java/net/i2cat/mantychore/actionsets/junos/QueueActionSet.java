package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.ConfirmAction;
import net.i2cat.mantychore.actionsets.junos.actions.IsAliveAction;
import net.i2cat.mantychore.actionsets.junos.actions.PrepareAction;
import net.i2cat.mantychore.actionsets.junos.actions.RestoreAction;
import net.i2cat.nexus.resources.action.ActionSet;

public class QueueActionSet extends ActionSet {

	public QueueActionSet() {
		super.setActionSetId("queueActionSet");
		this.putAction(ActionConstants.CONFIRM, ConfirmAction.class);
		this.putAction(ActionConstants.ISALIVE, IsAliveAction.class);
		this.putAction(ActionConstants.PREPARE, PrepareAction.class);
		this.putAction(ActionConstants.RESTORE, RestoreAction.class);
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.CONFIRM);
		actionNames.add(ActionConstants.ISALIVE);
		actionNames.add(ActionConstants.PREPARE);
		actionNames.add(ActionConstants.RESTORE);
		return actionNames;
	}

}