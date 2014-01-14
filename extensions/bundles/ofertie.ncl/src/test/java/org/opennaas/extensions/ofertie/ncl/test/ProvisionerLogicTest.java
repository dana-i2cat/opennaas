package org.opennaas.extensions.ofertie.ncl.test;

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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.NCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationRejectedException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Circuit;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QoSRequirements;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

public class ProvisionerLogicTest {

	final String			userId	= "alice";
	final String			netId	= "NET:1234";
	final String			flowId	= "FLOW:1";

	NCLProvisioner			provisioner;
	IQoSPDP					qosPDP;
	INetworkSelector		networkSelector;
	INCLController			nclController;
	IRequestToFlowsLogic	requestToFlowsLogic;

	NCLModel				model;

	FlowRequest				flowRequest;
	List<NetOFFlow>			sdnFlow;

	@Before
	public void initFlowRequest() {
		flowRequest = generateSampleFlow().getFlowRequest();
		sdnFlow = new ArrayList<NetOFFlow>();
	}

	@Before
	public void initProvisioner() {

		model = new NCLModel();

		qosPDP = createMock(IQoSPDP.class);
		networkSelector = createMock(INetworkSelector.class);
		nclController = createMock(INCLController.class);
		requestToFlowsLogic = createMock(IRequestToFlowsLogic.class);

		provisioner = new NCLProvisioner();
		provisioner.setModel(model);
		provisioner.setQoSPDP(qosPDP);
		provisioner.setNetworkSelector(networkSelector);
		provisioner.setNclController(nclController);
		provisioner.setRequestToFlowsLogic(requestToFlowsLogic);

	}

	@Test
	public void allocateFlowOkTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(true);
		expect(requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest)).andReturn(sdnFlow);
		expect(networkSelector.findNetworkForRequest(flowRequest)).andReturn(netId);

		nclController.allocateFlows(sdnFlow, netId);
		expectLastCall().once();

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		String result = provisioner.allocateFlow(flowRequest);
		Assert.assertNotNull(result);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

	@Test(expected = FlowAllocationRejectedException.class)
	public void allocateFlowQoSDeniedTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(false);

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		provisioner.allocateFlow(flowRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

	@Test(expected = ProvisionerException.class)
	public void allocateFlowNetworkSelectorErrorTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(true);
		expect(requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest)).andReturn(sdnFlow);
		expect(networkSelector.findNetworkForRequest(flowRequest)).andThrow(new Exception());

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		provisioner.allocateFlow(flowRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

	@Test(expected = FlowAllocationException.class)
	public void allocateFlowNCLControllerErrorTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, flowRequest)).andReturn(true);
		expect(requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(flowRequest)).andReturn(sdnFlow);
		expect(networkSelector.findNetworkForRequest(flowRequest)).andReturn(netId);

		nclController.allocateFlows(sdnFlow, netId);
		expectLastCall().andThrow(new FlowAllocationException());

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		provisioner.allocateFlow(flowRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

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
