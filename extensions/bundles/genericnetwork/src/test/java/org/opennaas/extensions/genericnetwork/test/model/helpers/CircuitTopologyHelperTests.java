package org.opennaas.extensions.genericnetwork.test.model.helpers;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.helpers.CircuitTopologyHelper;
import org.opennaas.extensions.genericnetwork.model.topology.Domain;
import org.opennaas.extensions.genericnetwork.model.topology.Link;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;

/**
 * Unit tests for {@link CircuitTopologyHelper}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class CircuitTopologyHelperTests {

	@Test
	public void networkElementNetworkConnectionMapTest() {
		// generate map
		Map<NetworkElement, NetworkConnection> networkConnectionsMap = CircuitTopologyHelper.networkElementNetworkConnectionMap(generateRoute(),
				generateTopology());

		Assert.assertEquals("Map must contain 3 elements", 3, networkConnectionsMap.size());

		// check D1 - NC1
		NetworkElement d1 = generateNetworkElement(Domain.class, "D1", 3);
		Assert.assertTrue("Map must contain D1", networkConnectionsMap.containsKey(d1));

		NetworkConnection nc1 = generateNetworkConnection("NC1", generatePortFromId("D1P1"), generatePortFromId("D1P3"));
		Assert.assertEquals("Map must contain the relation D1 - NC1", nc1, networkConnectionsMap.get(d1));

		// check D2 - NC3
		NetworkElement d2 = generateNetworkElement(Domain.class, "D2", 3);
		Assert.assertTrue("Map must contain D2", networkConnectionsMap.containsKey(d2));

		NetworkConnection nc3 = generateNetworkConnection("NC3", generatePortFromId("D2P1"), generatePortFromId("D2P3"));
		Assert.assertEquals("Map must contain the relation D2 - NC3", nc3, networkConnectionsMap.get(d2));

		// check D3 - NC5
		NetworkElement d3 = generateNetworkElement(Domain.class, "D3", 3);
		Assert.assertTrue("Map must contain D3", networkConnectionsMap.containsKey(d3));

		NetworkConnection nc5 = generateNetworkConnection("NC5", generatePortFromId("D3P1"), generatePortFromId("D3P3"));
		Assert.assertEquals("Map must contain the relation D3 - NC5", nc5, networkConnectionsMap.get(d3));
	}

	@Test
	public void getNetworkElementWithPortsTest() {
		Set<NetworkElement> networkElements = generateNetworkElements();

		NetworkElement ne = CircuitTopologyHelper.getNetworkElementWithPorts(networkElements, generatePortFromId("D1P1"), generatePortFromId("D1P3"));
		Assert.assertNotNull("Network Element must not be null", ne);
		Assert.assertTrue("NetworkElement must be a Domain", ne instanceof Domain);
		Domain domain = (Domain) ne;
		Assert.assertEquals("Domain must be D1", "D1", domain.getId());

		ne = CircuitTopologyHelper.getNetworkElementWithPorts(networkElements, generatePortFromId("D1P1"), generatePortFromId("D2P1"));
		Assert.assertNull("Network Element must be null", ne);

	}

	/**
	 * Generates a Route with this NetworkConnection's:<br>
	 * D1P1 -> D1P3 -> D2P1 -> D2P3 -> D3P1 -> D3P3
	 * 
	 * @return
	 */
	public static Route generateRoute() {
		Route route = new Route();

		route.setId("R1");

		List<NetworkConnection> networkConnections = new ArrayList<NetworkConnection>();
		networkConnections.add(generateNetworkConnection("NC1", generatePortFromId("D1P1"), generatePortFromId("D1P3")));
		networkConnections.add(generateNetworkConnection("NC2", generatePortFromId("D1P3"), generatePortFromId("D2P1")));
		networkConnections.add(generateNetworkConnection("NC3", generatePortFromId("D2P1"), generatePortFromId("D2P3")));
		networkConnections.add(generateNetworkConnection("NC4", generatePortFromId("D2P3"), generatePortFromId("D3P1")));
		networkConnections.add(generateNetworkConnection("NC5", generatePortFromId("D3P1"), generatePortFromId("D3P3")));
		route.setNetworkConnections(networkConnections);

		return route;
	}

	private static NetworkConnection generateNetworkConnection(String id, Port srcPort, Port dstPort) {
		NetworkConnection networkConnection = new NetworkConnection();
		networkConnection.setId(id);
		networkConnection.setName(id);
		networkConnection.setSource(srcPort);
		networkConnection.setDestination(dstPort);
		return networkConnection;
	}

	private static Topology generateTopology() {
		Topology topology = new Topology();
		topology.setNetworkElements(generateNetworkElements());
		topology.setLinks(generateLinks());
		return topology;
	}

	/**
	 * Generates this Links:<br>
	 * D1P3 -> D2P1 and D2P3 -> D3P1
	 * 
	 * @return
	 */
	private static Set<Link> generateLinks() {
		Set<Link> links = new HashSet<Link>();
		links.add(generateLink(generatePortFromId("D1P3"), generatePortFromId("D2P1")));
		links.add(generateLink(generatePortFromId("D2P3"), generatePortFromId("D3P1")));
		return links;
	}

	private static Link generateLink(Port srcPort, Port dstPort) {
		Link link = new Link();
		link.setSrcPort(srcPort);
		link.setDstPort(dstPort);
		return link;
	}

	/**
	 * Generates a Set<NetworkElement> with:
	 * 
	 * <pre>
	 * {@code
	 * Domain "D1" {
	 * 	Port "D1P1";
	 * 	Port "D1P2";
	 * 	Port "D1P3";
	 * }
	 * 
	 * Domain "D2" {
	 * 	Port "D2P1";
	 * 	Port "D2P2";
	 * 	Port "D2P3";
	 * }
	 * 
	 * Domain "D3" {
	 * 	Port "D3P1";
	 * 	Port "D3P2";
	 * 	Port "D3P3";
	 * }
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	private static Set<NetworkElement> generateNetworkElements() {
		Set<NetworkElement> networkElements = new HashSet<NetworkElement>();

		// add 3 domains with 3 ports each one
		networkElements.addAll(generateNetworkElementsSet(Domain.class, 3, 3));

		// add 3 switches with 3 ports each one
		networkElements.addAll(generateNetworkElementsSet(Switch.class, 3, 3));

		return networkElements;
	}

	private static Set<NetworkElement> generateNetworkElementsSet(Class<? extends NetworkElement> networkElementClass, int networkElementNum,
			int portNumber) {
		Set<NetworkElement> networkElementsSet = new HashSet<NetworkElement>();

		// add networkElementNum NetworkElements
		for (int i = 1; i <= networkElementNum; i++) {
			NetworkElement networkElement = generateNetworkElement(networkElementClass, ((networkElementClass == Domain.class) ? "D" : "S") + i,
					portNumber);
			networkElementsSet.add(networkElement);
		}

		return networkElementsSet;
	}

	private static NetworkElement generateNetworkElement(Class<? extends NetworkElement> networkElementClass, String id, int portNumber) {
		NetworkElement networkElement = null;
		try {
			networkElement = networkElementClass.newInstance();
			networkElement.setId(id);

			Set<Port> ports = new HashSet<Port>();
			// add portNumber ports
			for (int j = 1; j <= portNumber; j++) {
				ports.add(generatePortFromId(networkElement.getId() + "P" + j));
			}

			networkElement.setPorts(ports);
		} catch (Exception e) {
			// ignore, it should not happen
		}
		return networkElement;
	}

	private static Port generatePortFromId(String portId) {
		Port port = new Port();
		port.setId(portId);
		return port;
	}
}
