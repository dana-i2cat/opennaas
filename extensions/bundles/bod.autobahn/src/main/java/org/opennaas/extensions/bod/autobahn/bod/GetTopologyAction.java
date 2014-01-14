package org.opennaas.extensions.bod.autobahn.bod;

/*
 * #%L
 * OpenNaaS :: BoD :: Autobahn driver
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;

import java.util.ArrayList;
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
			interfaces.put(port.getAddress(), ParameterTranslator.createInterface(port, false));
		}
		for (PortType port : localPorts) {
			// Note that this will replace interfaces created above
			interfaces.put(port.getAddress(), ParameterTranslator.createInterface(port, true));
		}

		List<AutobahnLink> links = new ArrayList<AutobahnLink>();
		for (ServiceType service : services) {
			for (ReservationType reservation : service.getReservations()) {
				if (!isFinalState(reservation.getState())) {
					AutobahnLink link = updateLinkTo(interfaces, service, reservation);
					if (link != null) {
						// ".link" suffix is added in the key to avoid replacing potentially existing interfaces with same name
						interfaces.put(link.getSource().getName() + ".link", (AutobahnInterface) link.getSource());
						interfaces.put(link.getSink().getName() + ".link", (AutobahnInterface) link.getSink());
						links.add(link);
					}
				}
			}
		}

		model.getNetworkElements().addAll(interfaces.values());
		model.getNetworkElements().addAll(links);
	}

	private boolean isFinalState(int state)
	{
		return state >= 20;
	}

	private AutobahnLink createLink(AutobahnInterface source,
			AutobahnInterface sink,
			ServiceType service,
			ReservationType reservation)
	{

		AutobahnInterface sourceClientIface = createClientInterface(source, reservation.getStartPort(), reservation.getUserStartVlan());
		AutobahnInterface sinkClientIface = createClientInterface(sink, reservation.getEndPort(), reservation.getUserEndVlan());

		AutobahnLink link = new AutobahnLink();
		link.setName(service.getBodID());
		link.setSource(sourceClientIface);
		link.setSink(sinkClientIface);
		link.setBidirectional(reservation.isBidirectional());
		link.setReservation(reservation);
		link.setService(service);

		sourceClientIface.setLinkTo(link);
		if (link.isBidirectional())
			sinkClientIface.setLinkTo(link);

		return link;
	}

	private AutobahnInterface createClientInterface(AutobahnInterface iface, PortType port, int vlan) {
		AutobahnInterface clientInterface;
		if (vlan >= 0) {
			clientInterface = ParameterTranslator.createInterface(port, iface.isLocal());
			// there may be two interfaces with same name if generated name is a valid autobahn id
			clientInterface.setName(port.getAddress() + "." + port.getVlan());
			clientInterface.setServerInterface(iface);
		} else {
			clientInterface = iface;
		}
		return clientInterface;
	}

	private AutobahnLink updateLinkTo(Map<String, AutobahnInterface> interfaces,
			ServiceType service,
			ReservationType reservation)
	{
		AutobahnInterface source =
				interfaces.get(reservation.getStartPort().getAddress());
		AutobahnInterface sink =
				interfaces.get(reservation.getEndPort().getAddress());

		if (source != null && sink != null) {
			AutobahnLink link = createLink(source, sink, service, reservation);
			List<AutobahnInterface> ifaces = new ArrayList<AutobahnInterface>(2);
			ifaces.add(source);
			ifaces.add(sink);
			link.setRequestParameters(ParameterTranslator.createRequestParameters(reservation, ifaces));
			log.info("Discovered reservation " + service.getBodID() +
					" from " + source.getName() + " to " + sink.getName());
			return link;
		} else {
			return null;
		}
	}
}
