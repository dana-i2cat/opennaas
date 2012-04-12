package org.opennaas.extensions.bod.autobahn.bod;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import net.geant.autobahn.administration.Administration;
import net.geant.autobahn.administration.ServiceType;
import net.geant.autobahn.administration.ReservationType;
import net.geant.autobahn.useraccesspoint.PortType;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;
import org.opennaas.extensions.bod.autobahn.model.AutobahnLink;
import org.opennaas.extensions.network.model.NetworkModel;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

public class GetTopologyAction extends AutobahnAction
{
	public final static String ACTIONID = "getTopology";

	private final static Function<PortType,String> getAddress =
		new Function<PortType,String>() {
			@Override
			public String apply(PortType port) {
				return port.getAddress();
			}
		};

	private final Log log = LogFactory.getLog(GetTopologyAction.class);

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

			Iterable<PortType> ports =
				queryPorts(protocolSessionManager);
			Iterable<ServiceType> services =
				queryServices(protocolSessionManager);

			log.info("Discovered interfaces " +
					 Joiner.on(", ").join(transform(ports, getAddress)));

			updateModel((NetworkModel) modelToUpdate, ports, services);

			return ActionResponse.okResponse(getActionID());
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	protected Iterable<PortType>
		queryPorts(IProtocolSessionManager protocolSessionManager)
		throws ProtocolException, UserAccessPointException_Exception
	{
		UserAccessPoint userAccessPoint =
			getUserAccessPointService(protocolSessionManager);
		List<PortType> clientPorts = userAccessPoint.getAllClientPorts();
		List<PortType> idcpPorts = userAccessPoint.getIdcpPorts();
		return concat(clientPorts, idcpPorts);
	}

	protected Iterable<ServiceType>
		queryServices(IProtocolSessionManager protocolSessionManager)
		throws ProtocolException
	{
		Administration administration =
			getAdministrationService(protocolSessionManager);
		return administration.getServices();
	}

	private void updateModel(NetworkModel model,
							 Iterable<PortType> ports,
							 Iterable<ServiceType> services)
	{
		String userName = System.getProperty("user.name");

		Map<String,AutobahnInterface> interfaces = Maps.newHashMap();
		for (PortType port: ports) {
			interfaces.put(port.getAddress(), createInterface(port));
		}

		for (ServiceType service: services) {
			if (userName.equals(service.getUser().getName())) {
				for (ReservationType reservation: service.getReservations()) {
					if (!isFinalState(reservation.getState())) {
						updateLinkTo(interfaces, service, reservation);
					}
				}
			}
		}

		model.getNetworkElements().addAll(interfaces.values());
	}

	protected boolean isFinalState(int state)
	{
		return state >= 20;
	}

	protected AutobahnInterface createInterface(PortType port)
	{
		AutobahnInterface i = new AutobahnInterface();
		i.setPortType(port);
		i.setName(port.getAddress());
		return i;
	}

	protected AutobahnLink createLink(AutobahnInterface source,
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

	protected void updateLinkTo(Map<String,AutobahnInterface> interfaces,
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

