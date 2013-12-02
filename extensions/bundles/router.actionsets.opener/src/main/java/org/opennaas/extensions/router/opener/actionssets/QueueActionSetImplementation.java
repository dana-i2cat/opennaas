package org.opennaas.extensions.router.opener.actionssets;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.extensions.router.opener.actionssets.actions.DummyAction;

public class QueueActionSetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID			= "queueActionSetOPENER";
	public static final String	REFRESH_ACTION_OPENER	= "openerRefreshAction";

	public QueueActionSetImplementation() {
		super.setActionSetId(ACTIONSET_ID);

		this.putAction(QueueConstants.CONFIRM, DummyAction.class);
		this.putAction(QueueConstants.ISALIVE, DummyAction.class);
		this.putAction(QueueConstants.PREPARE, DummyAction.class);
		this.putAction(QueueConstants.RESTORE, DummyAction.class);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(QueueConstants.CONFIRM);
		actionNames.add(QueueConstants.ISALIVE);
		actionNames.add(QueueConstants.PREPARE);
		actionNames.add(QueueConstants.RESTORE);
		return actionNames;
	}

}
