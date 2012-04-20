package org.opennaas.extensions.bod.autobahn.bod;

import java.util.List;

import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;

import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;

import org.opennaas.extensions.bod.actionsets.dummy.ActionConstants;
import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;
import org.opennaas.extensions.bod.autobahn.model.AutobahnLink;

import org.opennaas.extensions.network.model.topology.Interface;

public class ShutdownConnectionAction extends AutobahnAction
{
	public final static String ACTIONID = ActionConstants.SHUTDOWNCONNECTION;

	public ShutdownConnectionAction()
	{
		setActionID(ACTIONID);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
		throws ActionException
	{
		try {
			log.info("Shuttting down connection between " + params);

			String serviceId = getServiceIdOfLink();

			UserAccessPoint userAccessPoint =
				getUserAccessPointService(protocolSessionManager);
			userAccessPoint.cancelService(serviceId);

			getInterface(0).setLinkTo(null);

			log.info("Cancelled service request " + serviceId);

			return ActionResponse.okResponse(getActionID());
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	protected String getServiceIdOfLink()
		throws ActionException
	{
		AutobahnInterface source = getInterface(0);
		AutobahnInterface sink = getInterface(1);
		AutobahnLink link = (AutobahnLink) source.getLinkTo();

		log.info(String.valueOf(link));
		if (link != null) {
			log.info(link.getSink().hashCode());
		}
		log.info(sink.hashCode());

		if (link == null || link.getSink() != sink) {
			throw new ActionException(source.toString() + " is not linked to " + sink);
		}

		return link.getService().getBodID();
	}

	protected AutobahnInterface getInterface(int index)
	{
		List<Interface> interfaces = (List<Interface>) params;
		return (AutobahnInterface) interfaces.get(index);
	}
}