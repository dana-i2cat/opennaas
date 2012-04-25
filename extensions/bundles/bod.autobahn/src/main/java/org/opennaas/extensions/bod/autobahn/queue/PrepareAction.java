package org.opennaas.extensions.bod.autobahn.queue;

import net.geant.autobahn.administration.Administration;

import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.queue.QueueConstants;

import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.commands.Transaction;

public class PrepareAction extends AutobahnAction
{
	public final static String ACTIONID = QueueConstants.PREPARE;

	public PrepareAction()
	{
		setActionID(ACTIONID);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
		throws ActionException
	{
		checkAlive(protocolSessionManager);
		Transaction.getInstance().begin();
		return ActionResponse.okResponse(getActionID());
	}

	private void checkAlive(IProtocolSessionManager protocolSessionManager)
		throws ActionException
	{
		try {
			Administration administration =
				getAdministrationService(protocolSessionManager);
			administration.getStatus();
		} catch (ProtocolException e) {
			throw new ActionException("Protocol failure: " + e.getMessage(), e);
		} catch (RuntimeException e) {
			throw new ActionException("BoD resource failed: " + e.toString(), e);
		}
	}
}