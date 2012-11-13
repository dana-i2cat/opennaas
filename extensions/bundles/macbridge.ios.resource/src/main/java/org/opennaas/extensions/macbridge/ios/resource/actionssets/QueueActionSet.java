package org.opennaas.extensions.macbridge.ios.resource.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.queue.ConfirmAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.queue.IsAliveAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.queue.PrepareAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.queue.RestoreAction;

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
