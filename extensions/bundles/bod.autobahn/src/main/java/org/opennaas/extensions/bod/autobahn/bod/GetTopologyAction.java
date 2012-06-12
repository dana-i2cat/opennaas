package org.opennaas.extensions.bod.autobahn.bod;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;

import java.util.List;
import java.util.Map;

import net.geant.autobahn.administration.Administration;
import net.geant.autobahn.administration.ReservationType;
import net.geant.autobahn.administration.ServiceType;
import net.geant.autobahn.useraccesspoint.PortType;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;
import org.opennaas.extensions.bod.autobahn.model.AutobahnLink;
import org.opennaas.extensions.network.model.NetworkModel;

import com.google.common.collect.Maps;

public class GetTopologyAction extends AutobahnAction
{
	public final static String	ACTIONID	= "getTopology";

	private final Log			log			= LogFactory.getLog(GetTopologyAction.class);

	public GetTopologyAction()
	{
		setActionID(ACTIONID);
	}

	@Override
	public ActionResponse
			execute(IProtocolSessionManager protocolSessionManager)
					throws ActionException
	{
		try {
			log.info("Retrieving Autobahn topology");

			Iterable<PortType> localPorts =
					queryLocalPorts(protocolSessionManager);
			Iterable<PortType> allPorts =
					queryAllPorts(protocolSessionManager);
			Iterable<ServiceType> services =
					queryServices(protocolSessionManager);

			updateModel((NetworkModel) modelToUpdate,
					localPorts, allPorts, services);

			return okResponse(allPorts, services);
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	private ActionResponse okResponse(Iterable<PortType> ports,
			Iterable<ServiceType> services)
	{
		ActionResponse response = new ActionResponse();
		response.setActionID(getActionID());
		response.setStatus(ActionResponse.STATUS.OK);
		response.setInformation("Discovered " + size(ports) + " ports " +
				"and " + size(services) + " services");
		for (PortType port : ports) {
			response.addResponse(newPortResponse(port));
		}
		return response;
	}

	private Response newPortResponse(PortType port)
	{
		return Response.okResponse("getPort", port.getAddress() +
				" (" + port.getDescription() +
				")");
	}

	private Iterable<PortType>
			queryLocalPorts(IProtocolSessionManager protocolSessionManager)
					throws ProtocolException, UserAccessPointException_Exception
	{
		UserAccessPoint userAccessPoint =
				getUserAccessPointService(protocolSessionManager);
		return userAccessPoint.getDomainClientPorts();
	}

	private Iterable<PortType>
			queryAllPorts(IProtocolSessionManager protocolSessionManager)
					throws ProtocolException, UserAccessPointException_Exception
	{
		UserAccessPoint userAccessPoint =
				getUserAccessPointService(protocolSessionManager);
		List<PortType> clientPorts = userAccessPoint.getAllClientPorts();
		List<PortType> idcpPorts = userAccessPoint.getIdcpPorts();
		return concat(clientPorts, idcpPorts);
	}

	private Iterable<ServiceType>
			queryServices(IProtocolSessionManager protocolSessionManager)
					throws ProtocolException
	{
		Administration administration =
				getAdministrationService(protocolSessionManager);
		return administration.getServices();
	}

	private void updateModel(NetworkModel model,
			Iterable<PortType> localPorts,
			Iterable<PortType> allPorts,
			Iterable<ServiceType> services)
	{
		Map<String, AutobahnInterface> interfaces = Maps.newHashMap();
		for (PortType port : allPorts) {
			interfaces.put(port.getAddress(), createInterface(port, false));
		}
		for (PortType port : localPorts) {
			// Note that this will replace interfaces created above
			interfaces.put(port.getAddress(), createInterface(port, true));
		}

		for (ServiceType service : services) {
			for (ReservationType reservation : service.getReservations()) {
				if (!isFinalState(reservation.getState())) {
					updateLinkTo(interfaces, service, reservation);
				}
			}
		}

		model.getNetworkElements().addAll(interfaces.values());
	}

	private boolean isFinalState(int state)
	{
		return state >= 20;
	}

	private AutobahnInterface createInterface(PortType port, boolean isLocal)
	{
		AutobahnInterface i = new AutobahnInterface();
		i.setPortType(port);
		i.setName(port.getAddress());
		i.setLocal(isLocal);
		return i;
	}

	private AutobahnLink createLink(AutobahnInterface source,
			AutobahnInterface sink,
			ServiceType service,
			ReservationType reservation)
	{
		AutobahnLink link = new AutobahnLink();
		link.setSource(source);
		link.setSink(sink);
		link.setBidirectional(reservation.isBidirectional());
		link.setReservation(reservation);
		link.setService(service);
		return link;
	}

	private void updateLinkTo(Map<String, AutobahnInterface> interfaces,
			ServiceType service,
			ReservationType reservation)
	{
		AutobahnInterface source =
				interfaces.get(reservation.getStartPort().getAddress());
		AutobahnInterface sink =
				interfaces.get(reservation.getEndPort().getAddress());

		if (source != null && sink != null) {
			source.setLinkTo(createLink(source, sink, service, reservation));
			log.info("Discovered reservation " + service.getBodID() +
					" from " + source.getName() + " to " + sink.getName());
		}
	}
}
