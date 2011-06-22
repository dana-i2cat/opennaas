package net.i2cat.mantychore.actionsets.junos;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.actions.ConfirmAction;
import net.i2cat.mantychore.actionsets.junos.actions.IsAliveAction;
import net.i2cat.mantychore.actionsets.junos.actions.PrepareAction;
import net.i2cat.mantychore.actionsets.junos.actions.RestoreAction;
import net.i2cat.nexus.resources.action.ActionSet;
import net.i2cat.nexus.resources.queue.QueueConstants;

public class QueueActionSet extends ActionSet {

	public QueueActionSet() {
		super.setActionSetId("queueActionSet");
		this.putAction(QueueConstants.CONFIRM, ConfirmAction.class);
		this.putAction(QueueConstants.ISALIVE, IsAliveAction.class);
		this.putAction(QueueConstants.PREPARE, PrepareAction.class);
		this.putAction(QueueConstants.RESTORE, RestoreAction.class);
	}

	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(QueueConstants.CONFIRM);
		actionNames.add(QueueConstants.ISALIVE);
		actionNames.add(QueueConstants.PREPARE);
		actionNames.add(QueueConstants.RESTORE);
		return actionNames;
	}

}