package org.opennaas.extensions.ofertie.ncl.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.RequestToFlowsLogic;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.Port;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

public class RequestToFlowsLogicTest {

	FlowRequest				flowRequest;
	Route					route;

	IRequestToFlowsLogic	requestToFlowsLogic;

	@Before
	public void initFlowRequest() {
		flowRequest = generateEmptyFlowRequest();
		route = generateSampleRoute();
	}

	@Before
	public void initLogic() {
		requestToFlowsLogic = new RequestToFlowsLogic();
	}

	@Test
	public void requestWithSrcIPTest() {
		String srcIp = "192.168.0.2";

		flowRequest.setSourceIPAddress(srcIp);

		SDNNetworkOFFlow flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(srcIp, flow.getMatch().getSrcIp());
		Assert.assertEquals("2048", flow.getMatch().getEtherType());
	}

	@Test
	public void requestWithDstIPTest() {
		String dstIp = "192.168.0.3";

		flowRequest.setDestinationIPAddress(dstIp);

		SDNNetworkOFFlow flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(dstIp, flow.getMatch().getDstIp());
		Assert.assertEquals("2048", flow.getMatch().getEtherType());
	}

	@Test
	public void requestWithIPsTest() {
		String srcIp = "192.168.0.2";
		String dstIp = "192.168.0.3";

		flowRequest.setSourceIPAddress(srcIp);
		flowRequest.setDestinationIPAddress(dstIp);

		SDNNetworkOFFlow flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(srcIp, flow.getMatch().getSrcIp());
		Assert.assertEquals(dstIp, flow.getMatch().getDstIp());
		Assert.assertEquals("2048", flow.getMatch().getEtherType());
	}

	@Test
	public void requestWithToSTest() {
		int tos = 1;

		flowRequest.setTos(tos);

		SDNNetworkOFFlow flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(tos, flow.getMatch().getTosBits());
		Assert.assertEquals("2048", flow.getMatch().getEtherType());
	}

	@Test
	public void requestWithIPsAndToSTest() {
		String srcIp = "192.168.0.2";
		String dstIp = "192.168.0.3";
		int tos = 1;

		flowRequest.setSourceIPAddress(srcIp);
		flowRequest.setDestinationIPAddress(dstIp);
		flowRequest.setTos(tos);

		SDNNetworkOFFlow flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(srcIp, flow.getMatch().getSrcIp());
		Assert.assertEquals(dstIp, flow.getMatch().getDstIp());
		Assert.assertEquals(tos, flow.getMatch().getTosBits());
		Assert.assertEquals("2048", flow.getMatch().getEtherType());
	}

	@Test
	public void requestWithPortTest() {
		int srcPort = 8080;
		int dstPort = 80;

		flowRequest.setSourcePort(srcPort);

		SDNNetworkOFFlow flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(String.valueOf(srcPort), flow.getMatch().getSrcPort());

		flowRequest.setSourcePort(0);
		flowRequest.setDestinationPort(dstPort);

		flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(String.valueOf(dstPort), flow.getMatch().getDstPort());

		flowRequest.setSourcePort(srcPort);
		flowRequest.setDestinationPort(dstPort);

		flow = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest, route);
		Assert.assertNotNull(flow);
		Assert.assertNotNull(flow.getMatch());
		Assert.assertEquals(String.valueOf(srcPort), flow.getMatch().getSrcPort());
		Assert.assertEquals(String.valueOf(dstPort), flow.getMatch().getDstPort());
	}

	private FlowRequest generateEmptyFlowRequest() {

		FlowRequest request = new FlowRequest();
		request.setRequestId("1");

		return request;
	}

	private Route generateSampleRoute() {

		Port src = new Port();
		src.setDeviceId("switch01");
		src.setPortNumber("1");

		Port dst = new Port();
		dst.setDeviceId("switch01");
		dst.setPortNumber("2");

		NetworkConnection connection = new NetworkConnection();
		connection.setSource(src);
		connection.setDestination(dst);
		connection.setName("switch01:1->switch01:2");

		Route route = new Route();
		route.setNetworkConnections(Arrays.asList(connection));

		return route;
	}

}
