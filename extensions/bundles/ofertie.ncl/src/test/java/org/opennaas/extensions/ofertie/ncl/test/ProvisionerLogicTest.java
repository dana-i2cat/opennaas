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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.INCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.NCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestHelper;
import org.opennaas.extensions.ofertie.ncl.helpers.QosPolicyRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.NCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationRejectedException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.NetworkSelectorMockup;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.QoSPDPMockup;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Isart Canyameres (i2CAT)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@RunWith(PowerMockRunner.class)
public class ProvisionerLogicTest {

	final String				userId	= "alice";
	final String				netId	= "genericnetwork:genericnet01";
	final String				flowId	= "FLOW:1";

	NCLProvisioner				provisioner;
	IQoSPDP						qosPDP;
	INetworkSelector			networkSelector;
	INCLProvisionerCapability	nclProvCapab;
	IResourceManager			resourceManager;
	Resource					resource;
	NCLProvisionerCapability	provisionerCapab;

	NCLModel					model;

	QosPolicyRequest			qosRequest;

	@Before
	public void initFlowRequest() {
		qosRequest = QoSPolicyRequestHelper.generateSampleQosPolicyRequest();
	}

	@Before
	public void initProvisioner() throws Exception {

		model = new NCLModel();

		qosPDP = mockQoSPDP();
		networkSelector = mockNetworkSelector();
		resource = createResource();
		resourceManager = mockResourceManager();

		provisioner = new NCLProvisioner();
		provisioner.setModel(model);
		provisioner.setQoSPDP(qosPDP);
		provisioner.setNetworkSelector(networkSelector);
		provisioner.setResourceManager(resourceManager);
	}

	/**
	 * Following test mocks the resourceManager to return a specific mocked resource which contain a mock NCLProvisionerCapability. The main goal of
	 * this test is to check that, when a valid QoSPolicyRequest is allocated, the NCLProvisioner model contains the corresponding CircuitRequest, and
	 * returns the same id to the user.
	 * 
	 * @throws Exception
	 */
	@Test
	public void allocateFlowOkTest() throws Exception {

		CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(qosRequest);

		PowerMockito.when(qosPDP.shouldAcceptRequest(userId, qosRequest)).thenReturn(true);
		PowerMockito.when(networkSelector.getNetwork()).thenReturn(netId);
		PowerMockito.doReturn(resource).when(resourceManager).getResourceById(netId);
		PowerMockito.doReturn(flowId).when(nclProvCapab).allocateCircuit(Mockito.any(CircuitRequest.class));

		String generatedFlow = provisioner.allocateFlow(qosRequest);

		Assert.assertEquals("Flow returned by the mock NCLProvisionerCapability should be the one given to PowerMockito", flowId, generatedFlow);
		Assert.assertNotNull("A CircuitRequest should have been added to NClProvisioner model.", model.getAllocatedRequests());
		Assert.assertEquals("A CircuitRequest should have been added to NClProvisioner model.", 1, model.getAllocatedRequests().size());
		Assert.assertTrue("NCLProvisioner model should contain CircuitRequest for flow " + flowId,
				model.getAllocatedRequests().keySet().contains(flowId));

		Assert.assertEquals("CircuitRequest stored at NCLProvisioner is not the expected.", circuitRequest, model.getAllocatedRequests().get(flowId));

	}

	@Test(expected = FlowAllocationRejectedException.class)
	public void allocateFlowQoSDeniedTest() throws Exception {

		PowerMockito.when(qosPDP.shouldAcceptRequest(userId, qosRequest)).thenReturn(false);
		provisioner.allocateFlow(qosRequest);

	}

	@Test(expected = ProvisionerException.class)
	public void allocateFlowNetworkSelectorErrorTest() throws Exception {

		CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(qosRequest);

		PowerMockito.when(qosPDP.shouldAcceptRequest(userId, qosRequest)).thenReturn(true);
		PowerMockito.when(networkSelector.getNetwork()).thenThrow(new Exception());

		provisioner.allocateFlow(qosRequest);

	}

	@Test(expected = FlowAllocationException.class)
	public void allocateFlowNCLControllerErrorTest() throws Exception {

		CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(qosRequest);

		PowerMockito.when(qosPDP.shouldAcceptRequest(userId, qosRequest)).thenReturn(true);
		PowerMockito.when(networkSelector.getNetwork()).thenReturn(netId);
		PowerMockito.doReturn(resource).when(resourceManager).getResourceById(netId);
		PowerMockito.when(nclProvCapab.allocateCircuit(circuitRequest)).thenThrow(CapabilityException.class);

		provisioner.allocateFlow(qosRequest);

	}

	private Resource createResource() {

		nclProvCapab = PowerMockito.mock(NCLProvisionerCapability.class);

		Resource resource = new Resource();
		resource.addCapability(nclProvCapab);

		return resource;
	}

	private INetworkSelector mockNetworkSelector() throws Exception {

		return PowerMockito.mock(NetworkSelectorMockup.class);

	}

	private IResourceManager mockResourceManager() {

		return PowerMockito.mock(ResourceManager.class);
	}

	private IQoSPDP mockQoSPDP() {

		return PowerMockito.mock(QoSPDPMockup.class);
	}

}
