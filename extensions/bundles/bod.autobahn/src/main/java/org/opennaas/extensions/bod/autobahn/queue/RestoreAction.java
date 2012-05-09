package org.opennaas.extensions.bod.autobahn.queue;

import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.queue.QueueConstants;

import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.commands.Transaction;

public class RestoreAction extends AutobahnAction
{
	public final static String ACTIONID = QueueConstants.RESTORE;

	public RestoreAction()
	{
		setActionID(ACTIONID);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
		throws ActionException
	{
		return Transaction.getInstance().rollback();
	}
}