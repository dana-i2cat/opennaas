package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NetworkConnection;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Port;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;

public class PathFinderMockup implements IPathFinder {

	private final static String			PATHS_FILE_URL	= "etc/org.opennaas.extensions.ofertie.ncl.paths.xml";

	private static final String			DEFAULT_ROUTE	= "0";

	/**
	 * Key: RouteId, Value: Route to apply
	 */
	private static Map<String, Route>	routes;

	private NCLModel					nclModel;

	private RouteSelectionLogic			routeSelectionLogic;

	public PathFinderMockup() throws IOException, SerializationException {

		String xmlRoutes = PathLoader.readXMLFile(PATHS_FILE_URL);
		routes = PathLoader.getRoutesFromXml(xmlRoutes);

	}

	public NCLModel getNclModel() {
		return nclModel;
	}

	public void setNclModel(NCLModel nclModel) {
		this.nclModel = nclModel;
	}

	/**
	 * @return the routeSelectionLogic
	 */
	public RouteSelectionLogic getRouteSelectionLogic() {
		return routeSelectionLogic;
	}

	/**
	 * @param routeSelectionLogic
	 *            the routeSelectionLogic to set
	 */
	public void setRouteSelectionLogic(RouteSelectionLogic routeSelectionLogic) {
		this.routeSelectionLogic = routeSelectionLogic;
	}

	@Override
	public Route findPathForRequest(QosPolicyRequest qosPolicyRequest)
			throws Exception {

		RouteSelectionInput input = createRouteSelectionInputFromRequest(qosPolicyRequest);
		List<String> possibleRouteIds = routeSelectionLogic.getPotentialRoutes(input);

		possibleRouteIds = filterNotCongestedRoutes(possibleRouteIds);

		if (possibleRouteIds.isEmpty())
			throw new Exception("Unable to find uncongested route for given request");

		return routes.get(possibleRouteIds.get(0));
	}

	private RouteSelectionInput createRouteSelectionInputFromRequest(QosPolicyRequest qosPolicyRequest) {
		return new RouteSelectionInput(
				qosPolicyRequest.getSource().getAddress(),
				qosPolicyRequest.getDestination().getAddress(),
				String.valueOf(qosPolicyRequest.getLabel()));
	}

	private List<String> filterNotCongestedRoutes(List<String> routes) {
		List<String> notCongested = new ArrayList<String>();
		for (String routeId : routes) {
			if (!isCongestedRoute(routeId)) {
				notCongested.add(routeId);
			}
		}
		return notCongested;
	}

	private boolean isCongestedRoute(String routeId) {
		Route route = routes.get(routeId);
		for (NetworkConnection connection : route.getNetworkConnections()) {
			if (nclModel.getCongestedPorts().contains(connection.getSource()) ||
					nclModel.getCongestedPorts().contains(connection.getDestination())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initialize routes according to testbed topology
	 * 
	 * @return
	 */
	private static Map<String, Route> initRoutes() {

		Map<String, Port> ports = generateTopologyPorts();
		Map<String, NetworkConnection> links = generateTopologyLinks(ports);
		Map<String, NetworkConnection> xconnects = generateXConnects(ports);
		Map<String, Route> routes = new HashMap<String, Route>();

		// medium one
		Route route0 = new Route();
		route0.setId("0");
		List<NetworkConnection> route0Path = new ArrayList<NetworkConnection>(5);
		route0Path.add(links.get("h1-0->s3-1"));
		route0Path.add(xconnects.get("s3-1->s3-2"));
		route0Path.add(links.get("s3-2->s4-1"));
		route0Path.add(xconnects.get("s4-1->s4-2"));
		route0Path.add(links.get("s4-2->h2-0"));
		route0.setNetworkConnections(route0Path);
		routes.put(route0.getId(), route0);

		// shortest one
		Route route1 = new Route();
		route1.setId("1");
		List<NetworkConnection> route1Path = new ArrayList<NetworkConnection>(3);
		route1Path.add(links.get("h1-1->s5-1"));
		route1Path.add(xconnects.get("s5-1->s5-2"));
		route1Path.add(links.get("s5-2->h2-1"));
		route1.setNetworkConnections(route1Path);
		routes.put(route1.getId(), route1);

		// largest one
		Route route2 = new Route();
		route2.setId("2");
		List<NetworkConnection> route2Path = new ArrayList<NetworkConnection>(7);
		route2Path.add(links.get("h1-2->s6-1"));
		route2Path.add(xconnects.get("s6-1->s6-2"));
		route2Path.add(links.get("s6-2->s7-1"));
		route2Path.add(xconnects.get("s7-1->s7-2"));
		route2Path.add(links.get("s7-2->s8-1"));
		route2Path.add(xconnects.get("s8-1->s8-2"));
		route2Path.add(links.get("s8-2->h2-2"));
		route2.setNetworkConnections(route2Path);
		routes.put(route2.getId(), route2);

		// inverse routes

		// medium one
		Route route0c = new Route();
		route0c.setId("10");
		List<NetworkConnection> route0cPath = new ArrayList<NetworkConnection>(5);
		route0cPath.add(links.get("h2-0->s4-2"));
		route0cPath.add(xconnects.get("s4-2->s4-1"));
		route0cPath.add(links.get("s4-1->s3-2"));
		route0cPath.add(xconnects.get("s3-2->s3-1"));
		route0cPath.add(links.get("s3-1->h1-0"));
		route0c.setNetworkConnections(route0cPath);
		routes.put(route0c.getId(), route0c);

		// shortest one
		Route route1c = new Route();
		route1c.setId("11");
		List<NetworkConnection> route1cPath = new ArrayList<NetworkConnection>(3);
		route1cPath.add(links.get("h2-1->s5-2"));
		route1cPath.add(xconnects.get("s5-2->s5-1"));
		route1cPath.add(links.get("s5-1->h1-1"));
		route1c.setNetworkConnections(route1cPath);
		routes.put(route1c.getId(), route1c);

		// largest one
		Route route2c = new Route();
		route2c.setId("12");
		List<NetworkConnection> route2cPath = new ArrayList<NetworkConnection>(7);
		route2cPath.add(links.get("h2-2->s8-2"));
		route2cPath.add(xconnects.get("s8-2->s8-1"));
		route2cPath.add(links.get("s8-1->s7-2"));
		route2cPath.add(xconnects.get("s7-2->s7-1"));
		route2cPath.add(links.get("s7-1->s6-2"));
		route2cPath.add(xconnects.get("s6-2->s6-1"));
		route2cPath.add(links.get("s6-1->h1-2"));
		route2c.setNetworkConnections(route2cPath);
		routes.put(route2c.getId(), route2c);

		return routes;
	}

	private static Map<String, NetworkConnection> generateXConnects(Map<String, Port> ports) {
		NetworkConnection xconnect;
		Map<String, NetworkConnection> xconnects = new HashMap<String, NetworkConnection>();
		for (Port srcPort : ports.values()) {
			for (Port dstPort : ports.values()) {
				if (!srcPort.equals(dstPort)) {
					if (srcPort.getDeviceId().equals(dstPort.getDeviceId())) {
						if (srcPort.getDeviceId().startsWith("s")) {
							// device is a switch

							xconnect = new NetworkConnection();
							xconnect.setSource(srcPort);
							xconnect.setDestination(dstPort);
							xconnect.setName(xconnect.getSource().getId() + "->" + xconnect.getDestination().getId());
							xconnects.put(xconnect.getName(), xconnect);
						}
					}
				}
			}
		}

		return xconnects;
	}

	private static Map<String, NetworkConnection> generateTopologyLinks(Map<String, Port> ports) {

		NetworkConnection link;
		Map<String, NetworkConnection> links = new HashMap<String, NetworkConnection>();

		// longest route
		link = new NetworkConnection();
		link.setSource(ports.get("h2-2"));
		link.setDestination(ports.get("s8-2"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		link = new NetworkConnection();
		link.setSource(ports.get("s8-1"));
		link.setDestination(ports.get("s7-2"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		link = new NetworkConnection();
		link.setSource(ports.get("s6-2"));
		link.setDestination(ports.get("s7-1"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		link = new NetworkConnection();
		link.setSource(ports.get("h1-2"));
		link.setDestination(ports.get("s6-1"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		// shortest route
		link = new NetworkConnection();
		link.setSource(ports.get("s5-2"));
		link.setDestination(ports.get("h2-1"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		link = new NetworkConnection();
		link.setSource(ports.get("h1-1"));
		link.setDestination(ports.get("s5-1"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		// medium route
		link = new NetworkConnection();
		link.setSource(ports.get("s4-2"));
		link.setDestination(ports.get("h2-0"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		link = new NetworkConnection();
		link.setSource(ports.get("s3-2"));
		link.setDestination(ports.get("s4-1"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		link = new NetworkConnection();
		link.setSource(ports.get("h1-0"));
		link.setDestination(ports.get("s3-1"));
		link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
		links.put(link.getName(), link);

		// calculate inverse links
		Map<String, NetworkConnection> inverseLinks = new HashMap<String, NetworkConnection>();
		for (NetworkConnection nc : links.values()) {
			link = new NetworkConnection();
			link.setSource(nc.getDestination());
			link.setDestination(nc.getSource());
			link.setName(link.getSource().getId() + "->" + link.getDestination().getId());
			inverseLinks.put(link.getName(), link);
		}
		links.putAll(inverseLinks);

		return links;
	}

	private static Map<String, Port> generateTopologyPorts() {

		Port port;
		Map<String, Port> ports = new HashMap<String, Port>();

		// h1 ports
		port = new Port();
		port.setDeviceId("h1");
		port.setPortNumber("0");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("h1");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("h1");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		// h2 ports
		port = new Port();
		port.setDeviceId("h2");
		port.setPortNumber("0");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("h2");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("h2");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		// s3 ports
		port = new Port();
		port.setDeviceId("s3");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("s3");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		// s4 ports
		port = new Port();
		port.setDeviceId("s4");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("s4");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		// s5 ports
		port = new Port();
		port.setDeviceId("s5");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("s5");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		// s6 ports
		port = new Port();
		port.setDeviceId("s6");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("s6");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		// s7 ports
		port = new Port();
		port.setDeviceId("s7");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("s7");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		// s8 ports
		port = new Port();
		port.setDeviceId("s8");
		port.setPortNumber("1");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);
		port = new Port();
		port.setDeviceId("s8");
		port.setPortNumber("2");
		ports.put(port.getDeviceId() + "-" + port.getPortNumber(), port);

		return ports;
	}

}
