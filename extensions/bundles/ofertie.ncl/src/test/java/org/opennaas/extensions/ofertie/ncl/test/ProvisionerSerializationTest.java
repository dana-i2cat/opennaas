package org.opennaas.extensions.ofertie.ncl.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Circuit;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QoSRequirements;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing.RouteIds;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing.RouteSelectionInput;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing.RouteSelectionMap;

public class ProvisionerSerializationTest {

	@Test
	public void flowSerializationTest() throws SerializationException {
		Circuit original = generateSampleFlow();
		String xml = ObjectSerializer.toXml(original);
		Circuit generated = (Circuit) ObjectSerializer.fromXml(xml, Circuit.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
	}

	@Test
	public void flowRequestSerializationTest() throws SerializationException {
		FlowRequest original = generateSampleFlow().getFlowRequest();
		String xml = ObjectSerializer.toXml(original);
		FlowRequest generated = (FlowRequest) ObjectSerializer.fromXml(xml, FlowRequest.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
	}

	@Test
	public void routeSelectionMapSerializationTest() throws SerializationException {
		RouteSelectionMap original = generateSampleRouteSelectionMap();
		String xml = ObjectSerializer.toXml(original);
		RouteSelectionMap generated = (RouteSelectionMap) ObjectSerializer.fromXml(xml, RouteSelectionMap.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
		System.out.println(xml);
	}

	// <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	// <routeSelectionMap>
	// <routeMapping>
	// <entry>
	// <key>
	// <srcIP>192.168.1.1</srcIP>
	// <dstIP>192.168.1.2</dstIP>
	// <tos>4</tos>
	// </key>
	// <value>
	// <routeIds>1</routeIds>
	// <routeIds>2</routeIds>
	// <routeIds>3</routeIds>
	// </value>
	// </entry>
	// <entry>
	// <key>
	// <srcIP>192.168.1.2</srcIP>
	// <dstIP>192.168.1.1</dstIP>
	// <tos>4</tos>
	// </key>
	// <value>
	// <routeIds>3</routeIds>
	// <routeIds>4</routeIds>
	// <routeIds>5</routeIds>
	// <routeIds>1</routeIds>
	// </value>
	// </entry>
	// </routeMapping>
	// </routeSelectionMap>
	private RouteSelectionMap generateSampleRouteSelectionMap() {

		String srcIP = "192.168.1.1";
		String dstIP = "192.168.1.2";
		String tos = "4";

		RouteSelectionInput input1 = new RouteSelectionInput(srcIP, dstIP, tos);
		RouteSelectionInput input2 = new RouteSelectionInput(dstIP, srcIP, tos);

		List<String> routes1 = Arrays.asList("1", "2", "3");
		List<String> routes2 = Arrays.asList("3", "4", "5", "1");

		RouteIds ids1 = new RouteIds();
		ids1.setRouteIds(routes1);

		RouteIds ids2 = new RouteIds();
		ids2.setRouteIds(routes2);

		Map<RouteSelectionInput, RouteIds> routeMapping = new HashMap<RouteSelectionInput, RouteIds>();
		routeMapping.put(input1, ids1);
		routeMapping.put(input2, ids2);

		RouteSelectionMap routeMap = new RouteSelectionMap();
		routeMap.setRouteMapping(routeMapping);

		return routeMap;
	}

	// <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	// <flow>
	// <id>1</id>
	// <flowRequest>
	// <requestId>1</requestId>
	// <sourceIPAddress>192.168.0.1</sourceIPAddress>
	// <destinationIPAddress>192.168.0.2</destinationIPAddress>
	// <sourcePort>8080</sourcePort>
	// <destinationPort>8080</destinationPort>
	// <tos>1</tos>
	// <sourceVlanId>1100</sourceVlanId>
	// <destinationVlanId>1100</destinationVlanId>
	// <qoSRequirements>
	// <minDelay>-1</minDelay>
	// <maxDelay>10</maxDelay>
	// <minJitter>-1</minJitter>
	// <maxJitter>10</maxJitter>
	// <minBandwidth>100000000</minBandwidth>
	// <maxBandwidth>-1</maxBandwidth>
	// <minPacketLoss>-1</minPacketLoss>
	// <maxPacketLoss>10</maxPacketLoss>
	// </qoSRequirements>
	// </flowRequest>
	// </flow>
	private Circuit generateSampleFlow() {

		QoSRequirements qoSRequirements = new QoSRequirements();
		qoSRequirements.setMinBandwidth(100 * 1000 * 1000);
		qoSRequirements.setMaxBandwidth(-1);
		qoSRequirements.setMinDelay(-1);
		qoSRequirements.setMaxDelay(10);
		qoSRequirements.setMinJitter(-1);
		qoSRequirements.setMaxJitter(10);
		qoSRequirements.setMinPacketLoss(-1);
		qoSRequirements.setMaxPacketLoss(10);

		FlowRequest request = new FlowRequest();
		request.setRequestId("1");
		request.setSourceIPAddress("192.168.0.1");
		request.setDestinationIPAddress("192.168.0.2");
		request.setSourcePort(8080);
		request.setDestinationPort(8080);
		request.setSourceVlanId(1100);
		request.setDestinationVlanId(1100);
		request.setTos(1);
		request.setQoSRequirements(qoSRequirements);

		Circuit flow = new Circuit();
		flow.setId("1");
		flow.setFlowRequest(request);

		return flow;
	}

}
