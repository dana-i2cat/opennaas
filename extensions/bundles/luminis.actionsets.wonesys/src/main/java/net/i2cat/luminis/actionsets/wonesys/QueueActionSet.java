package net.i2cat.luminis.actionsets.wonesys;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.luminis.actionsets.wonesys.actions.queue.ConfirmAction;
import net.i2cat.luminis.actionsets.wonesys.actions.queue.IsAliveAction;
import net.i2cat.luminis.actionsets.wonesys.actions.queue.PrepareAction;
import net.i2cat.luminis.actionsets.wonesys.actions.queue.RestoreAction;
import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.queue.QueueConstants;

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
