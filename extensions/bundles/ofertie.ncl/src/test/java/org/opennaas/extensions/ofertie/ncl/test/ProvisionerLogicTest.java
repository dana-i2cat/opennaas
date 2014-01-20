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
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
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

	QosPolicyRequest		qosPolicyRequest;
	List<NetOFFlow>			sdnFlow;

	@Before
	public void initFlowRequest() {
		qosPolicyRequest = generateSampleQosPolicyRequest();
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
		expect(qosPDP.shouldAcceptRequest(userId, qosPolicyRequest)).andReturn(true);
		expect(requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(qosPolicyRequest)).andReturn(sdnFlow);
		expect(networkSelector.findNetworkForRequest(qosPolicyRequest)).andReturn(netId);

		nclController.allocateFlows(sdnFlow, netId);
		expectLastCall().once();

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		String result = provisioner.allocateFlow(qosPolicyRequest);
		Assert.assertNotNull(result);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

	@Test(expected = FlowAllocationRejectedException.class)
	public void allocateFlowQoSDeniedTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, qosPolicyRequest)).andReturn(false);

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		provisioner.allocateFlow(qosPolicyRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

	@Test(expected = ProvisionerException.class)
	public void allocateFlowNetworkSelectorErrorTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, qosPolicyRequest)).andReturn(true);
		expect(requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(qosPolicyRequest)).andReturn(sdnFlow);
		expect(networkSelector.findNetworkForRequest(qosPolicyRequest)).andThrow(new Exception());

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		provisioner.allocateFlow(qosPolicyRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

	@Test(expected = FlowAllocationException.class)
	public void allocateFlowNCLControllerErrorTest() throws Exception {
		expect(qosPDP.shouldAcceptRequest(userId, qosPolicyRequest)).andReturn(true);
		expect(requestToFlowsLogic.getRequiredFlowsToSatisfyRequest(qosPolicyRequest)).andReturn(sdnFlow);
		expect(networkSelector.findNetworkForRequest(qosPolicyRequest)).andReturn(netId);

		nclController.allocateFlows(sdnFlow, netId);
		expectLastCall().andThrow(new FlowAllocationException());

		replay(qosPDP);
		replay(networkSelector);
		replay(requestToFlowsLogic);
		replay(nclController);

		provisioner.allocateFlow(qosPolicyRequest);

		verify(qosPDP);
		verify(networkSelector);
		verify(requestToFlowsLogic);
		verify(nclController);
	}

	private QosPolicyRequest generateSampleQosPolicyRequest() {

		QosPolicyRequest req = new QosPolicyRequest();

		Source source = new Source();
		source.setAddress("192.168.0.1");
		source.setPort("8080");
		req.setSource(source);

		Destination destination = new Destination();
		destination.setAddress("192.168.0.2");
		destination.setPort("8080");
		req.setDestination(destination);

		req.setLabel("1");

		QosPolicy qosPolicy = new QosPolicy();

		Latency latency = new Latency();
		latency.setMin(null);
		latency.setMax("10");
		qosPolicy.setLatency(latency);

		Jitter jitter = new Jitter();
		jitter.setMin(null);
		jitter.setMax("10");
		qosPolicy.setJitter(jitter);

		Throughput throughput = new Throughput();
		throughput.setMin(String.valueOf(100 * 1000 * 1000));
		throughput.setMax(null);
		qosPolicy.setThroughput(throughput);

		PacketLoss packetLoss = new PacketLoss();
		packetLoss.setMin(null);
		packetLoss.setMax("10");
		qosPolicy.setPacketLoss(packetLoss);

		req.setQosPolicy(qosPolicy);

		return req;
	}

}
