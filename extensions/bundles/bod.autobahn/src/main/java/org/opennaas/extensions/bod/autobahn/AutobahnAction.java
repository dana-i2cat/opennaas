package org.opennaas.extensions.bod.autobahn;

import net.geant.autobahn.administration.Administration;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

import org.opennaas.extensions.bod.autobahn.protocol.AutobahnProtocolSession;

public abstract class AutobahnAction extends Action
{
	private final static String AUTOBAHN = "autobahn";

	@Override
	public boolean checkParams(Object params) throws ActionException
	{
		return true;
	}

	protected AutobahnProtocolSession
		getAutobahnProtocolSession(IProtocolSessionManager manager)
		throws ProtocolException
	{
		return (AutobahnProtocolSession)
			manager.obtainSessionByProtocol(AUTOBAHN, false);
	}

	protected UserAccessPoint
		getUserAccessPointService(IProtocolSessionManager manager)
		throws ProtocolException
	{
		return getAutobahnProtocolSession(manager).getUserAccessPointService();
	}

	protected Administration
		getAdministrationService(IProtocolSessionManager manager)
		throws ProtocolException
	{
		return getAutobahnProtocolSession(manager).getAdministrationService();
	}
}
