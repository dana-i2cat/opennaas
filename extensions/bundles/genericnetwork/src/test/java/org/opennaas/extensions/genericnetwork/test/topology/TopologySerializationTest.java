package org.opennaas.extensions.genericnetwork.test.topology;

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
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.genericnetwork.model.topology.Domain;
import org.opennaas.extensions.genericnetwork.model.topology.Link;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.genericnetwork.model.topology.TopologyElementState;

public class TopologySerializationTest {

	private Topology	topology;

	@Before
	public void initTopology() {
		topology = generateSampleTopology();
	}

	@Test
	public void topologySerializationDeserializationTest() throws SerializationException {
		inverseSerializationTest(topology);
	}

	@Test
	public void linkDeserializationUsesPortReferencesTest() throws SerializationException {
		String xml = ObjectSerializer.toXml(topology);
		Topology generated = (Topology) ObjectSerializer.fromXml(xml, Topology.class);

		List<Port> ports = new ArrayList<Port>();
		for (NetworkElement ne : generated.getNetworkElements()) {
			ports.addAll(ne.getPorts());
		}

		List<Port> referencedPorts = new ArrayList<Port>();
		for (Link link : generated.getLinks()) {
			referencedPorts.add(link.getSrcPort());
			referencedPorts.add(link.getDstPort());
		}

		boolean isReferenced;
		for (Port referenced : referencedPorts) {
			isReferenced = false;
			for (Port p : ports) {
				if (referenced == p) {
					isReferenced = true;
					break;
				}
			}
			Assert.assertTrue("Port " + referenced.getId() + " must be a reference to an existing port", isReferenced);
		}
	}

	/**
	 * Checks that serialization s(x) and deserialization d(x) are inverse functions one of the other. It does so by checking that composition of
	 * these methods form the identity: d(s(x)) = x
	 * 
	 * @param x
	 * @throws SerializationException
	 */
	private Object inverseSerializationTest(Object x) throws SerializationException {
		String xml = ObjectSerializer.toXml(x);
		System.out.println(xml);
		Object generated = ObjectSerializer.fromXml(xml, x.getClass());
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(x, generated);
		return generated;
	}

	/**
	 * Creates a sample topology forming a triangle with 2 switches and a domain, having 2 ports each.
	 * 
	 * @return
	 */
	private Topology generateSampleTopology() {

		Port p1 = new Port();
		p1.setId("1");
		p1.setState(new TopologyElementState());
		p1.getState().setCongested(false);

		Port p2 = new Port();
		p2.setId("2");
		p2.setState(new TopologyElementState());
		p2.getState().setCongested(true);

		Port p3 = new Port();
		p3.setId("3");

		Port p4 = new Port();
		p4.setId("4");

		Port p5 = new Port();
		p5.setId("5");

		Port p6 = new Port();
		p6.setId("6");

		Set<Port> s1ports = new HashSet<Port>();
		s1ports.add(p1);
		s1ports.add(p2);

		Set<Port> s2ports = new HashSet<Port>();
		s2ports.add(p3);
		s2ports.add(p4);

		Set<Port> d1ports = new HashSet<Port>();
		d1ports.add(p5);
		d1ports.add(p6);

		Switch s1 = new Switch();
		s1.setId("ofswitch:s1");
		s1.setPorts(s1ports);

		Switch s2 = new Switch();
		s2.setId("ofswitch:s2");
		s2.setPorts(s2ports);

		Domain d1 = new Domain();
		d1.setId("genericnetwork:n1");
		d1.setPorts(d1ports);

		Set<NetworkElement> networkElements = new HashSet<NetworkElement>();
		networkElements.add(s1);
		networkElements.add(s2);
		networkElements.add(d1);

		Link link1 = new Link();
		link1.setSrcPort(p2);
		link1.setDstPort(p3);

		Link link2 = new Link();
		link2.setSrcPort(p4);
		link2.setDstPort(p5);

		Link link3 = new Link();
		link3.setSrcPort(p6);
		link3.setDstPort(p1);

		Set<Link> links = new HashSet<Link>();
		links.add(link1);
		links.add(link2);
		links.add(link3);

		Topology topology = new Topology();
		topology.setLinks(links);
		topology.setNetworkElements(networkElements);
		return topology;

	}

}
