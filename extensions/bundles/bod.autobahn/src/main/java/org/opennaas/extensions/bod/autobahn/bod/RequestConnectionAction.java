package org.opennaas.extensions.bod.autobahn.bod;

import javax.xml.datatype.DatatypeConfigurationException;

import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.commands.IAutobahnCommand;
import org.opennaas.extensions.bod.autobahn.commands.SubmitServiceCommand;
import org.opennaas.extensions.bod.autobahn.commands.Transaction;
import org.opennaas.extensions.bod.capability.l2bod.L2BoDActionSet;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;

public class RequestConnectionAction extends AutobahnAction
{
	public final static String	ACTIONID	= L2BoDActionSet.REQUEST_CONNECTION;

	public RequestConnectionAction()
	{
		setActionID(ACTIONID);
	}

	@Override
	public ActionResponse
			execute(IProtocolSessionManager protocolSessionManager)
					throws ActionException
	{
		try {
			UserAccessPoint userAccessPoint =
					getUserAccessPointService(protocolSessionManager);
			ServiceRequest serviceRequest =
					ParameterTranslator.createServiceRequest((RequestConnectionParameters) params);
			IAutobahnCommand command =
					new SubmitServiceCommand(userAccessPoint, serviceRequest);

			Transaction.getInstance().add(command);

			return ActionResponse.okResponse(getActionID());
		} catch (DatatypeConfigurationException e) {
			throw new ActionException(e);
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

}
