package org.opennaas.extensions.ofertie.ncl.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.RequestToFlowsLogic;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NetworkConnection;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Port;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

public class RequestToFlowsLogicTest {

	FlowRequest			flowRequest;
	Route				route;

	RequestToFlowsLogic	requestToFlowsLogic;
	IPathFinder			pathFinder;

	@Before
	public void initFlowRequest() throws Exception {
		flowRequest = generateEmptyFlowRequest();
		route = generateSampleRoute();

		initLogic();
	}

	public void initLogic() throws Exception {
		pathFinder = createMock(IPathFinder.class);
		requestToFlowsLogic = new RequestToFlowsLogic();
		requestToFlowsLogic.setPathFinder(pathFinder);

		expect(pathFinder.findPathForRequest(flowRequest)).andReturn(route).anyTimes();
		replay(pathFinder);

	}

	@Test
	public void requestWithSrcIPTest() throws Exception {
		String srcIp = "192.168.0.2";

		flowRequest.setSourceIPAddress(srcIp);

		List<NetOFFlow> flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(srcIp, flow.getMatch().getSrcIp());
			Assert.assertEquals("2048", flow.getMatch().getEtherType());
		}
		verify(pathFinder);
	}

	@Test
	public void requestWithDstIPTest() throws Exception {
		String dstIp = "192.168.0.3";

		flowRequest.setDestinationIPAddress(dstIp);

		List<NetOFFlow> flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(dstIp, flow.getMatch().getDstIp());
			Assert.assertEquals("2048", flow.getMatch().getEtherType());
		}
		verify(pathFinder);
	}

	@Test
	public void requestWithIPsTest() throws Exception {
		String srcIp = "192.168.0.2";
		String dstIp = "192.168.0.3";

		flowRequest.setSourceIPAddress(srcIp);
		flowRequest.setDestinationIPAddress(dstIp);

		List<NetOFFlow> flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(srcIp, flow.getMatch().getSrcIp());
			Assert.assertEquals(dstIp, flow.getMatch().getDstIp());
			Assert.assertEquals("2048", flow.getMatch().getEtherType());
		}
		verify(pathFinder);
	}

	@Test
	public void requestWithToSTest() throws Exception {
		int tos = 4;
		int flowTos = tos / 4; // last 2 bits discarded

		flowRequest.setTos(tos);

		List<NetOFFlow> flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(String.valueOf(flowTos), flow.getMatch().getTosBits());
			Assert.assertEquals("2048", flow.getMatch().getEtherType());
		}
		verify(pathFinder);
	}

	@Test
	public void requestWithIPsAndToSTest() throws Exception {
		String srcIp = "192.168.0.2";
		String dstIp = "192.168.0.3";
		int tos = 4;
		int flowTos = tos / 4; // last 2 bits discarded

		flowRequest.setSourceIPAddress(srcIp);
		flowRequest.setDestinationIPAddress(dstIp);
		flowRequest.setTos(tos);

		List<NetOFFlow> flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(srcIp, flow.getMatch().getSrcIp());
			Assert.assertEquals(dstIp, flow.getMatch().getDstIp());
			Assert.assertEquals(String.valueOf(flowTos), flow.getMatch().getTosBits());
			Assert.assertEquals("2048", flow.getMatch().getEtherType());
		}
		verify(pathFinder);
	}

	@Test
	public void requestWithPortTest() throws Exception {
		int srcPort = 8080;
		int dstPort = 80;

		flowRequest.setSourcePort(srcPort);

		List<NetOFFlow> flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(String.valueOf(srcPort), flow.getMatch().getSrcPort());
		}

		flowRequest.setSourcePort(0);
		flowRequest.setDestinationPort(dstPort);

		flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(String.valueOf(dstPort), flow.getMatch().getDstPort());
		}

		flowRequest.setSourcePort(srcPort);
		flowRequest.setDestinationPort(dstPort);

		flows = requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest);
		for (NetOFFlow flow : flows) {
			Assert.assertNotNull(flow);
			Assert.assertNotNull(flow.getMatch());
			Assert.assertEquals(String.valueOf(srcPort), flow.getMatch().getSrcPort());
			Assert.assertEquals(String.valueOf(dstPort), flow.getMatch().getDstPort());
		}
		verify(pathFinder);
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
