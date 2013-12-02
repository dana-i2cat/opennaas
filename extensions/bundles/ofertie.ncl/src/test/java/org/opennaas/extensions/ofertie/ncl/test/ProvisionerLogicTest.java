package org.opennaas.extensions.ofertie.ncl.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.NCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationRejectedException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QoSRequirements;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;
import org.opennaas.extensions.sdnnetwork.model.Route;

public class ProvisionerLogicTest {

	final String		userId	= "alice";
	final String		netId	= "NET:1234";
	final Route			route	= new Route();
	final String		flowId	= "FLOW:1";

	NCLProvisioner		provisioner;
	IQoSPDP				qosPDP;
	INetworkSelector	networkSelector;
	IPathFinder			pathFinder;
	INCLController		nclController;

	FlowRequest			flowRequest;

	@Before
	public void initFlowRequest() {
		flowRequest = generateSampleFlow().getFlowRequest();
	}

	@Before
	public void initProvisioner() {

		qosPDP = createMock(IQoSPDP.class);
		networkSelector = createMock(INetworkSelector.class);
		pathFinder = createMock(IPathFinder.class);
		nclController = createMock(INCLController.class);

		provisioner = new NCLProvisioner();
		provisioner.setQoSPDP(qosPDP);
		provisioner.setNetworkSelector(networkSelector);
		provisioner.setPathFinder(pathFinder);
		provisioner.setNclController(nclController);
	}

	@Test
	public void allocateFlowOkTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(true);
		expect(networkSelector.findNetworkForRequest(flowRequest)).andReturn(netId);
		expect(pathFinder.findPathForRequest(flowRequest, netId)).andReturn(route);
		expect(nclController.allocateFlow(flowRequest, route, netId)).andReturn(flowId);

		replay(qosPDP);
		replay(networkSelector);
		replay(pathFinder);
		replay(nclController);

		String result = provisioner.allocateFlow(flowRequest);
		Assert.assertEquals(flowId, result);

		verify(qosPDP);
		verify(networkSelector);
		verify(pathFinder);
		verify(nclController);
	}

	@Test(expected = FlowAllocationRejectedException.class)
	public void allocateFlowQoSDeniedTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(false);

		replay(qosPDP);
		replay(networkSelector);
		replay(pathFinder);
		replay(nclController);

		provisioner.allocateFlow(flowRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(pathFinder);
		verify(nclController);
	}

	@Test(expected = ProvisionerException.class)
	public void allocateFlowNetworkSelectorErrorTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(true);
		expect(networkSelector.findNetworkForRequest(flowRequest)).andThrow(new Exception());

		replay(qosPDP);
		replay(networkSelector);
		replay(pathFinder);
		replay(nclController);

		provisioner.allocateFlow(flowRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(pathFinder);
		verify(nclController);
	}

	@Test(expected = ProvisionerException.class)
	public void allocateFlowPathFinderErrorTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(true);
		expect(networkSelector.findNetworkForRequest(flowRequest)).andReturn(netId);
		expect(pathFinder.findPathForRequest(flowRequest, netId)).andThrow(new Exception());

		replay(qosPDP);
		replay(networkSelector);
		replay(pathFinder);
		replay(nclController);

		provisioner.allocateFlow(flowRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(pathFinder);
		verify(nclController);
	}

	@Test(expected = FlowAllocationException.class)
	public void allocateFlowNCLControllerErrorTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(true);
		expect(networkSelector.findNetworkForRequest(flowRequest)).andReturn(netId);
		expect(pathFinder.findPathForRequest(flowRequest, netId)).andReturn(route);
		expect(nclController.allocateFlow(flowRequest, route, netId)).andThrow(new FlowAllocationException());

		replay(qosPDP);
		replay(networkSelector);
		replay(pathFinder);
		replay(nclController);

		provisioner.allocateFlow(flowRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(pathFinder);
		verify(nclController);
	}

	private Flow generateSampleFlow() {

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

		Flow flow = new Flow();
		flow.setId("1");
		flow.setFlowRequest(request);

		return flow;
	}

}
