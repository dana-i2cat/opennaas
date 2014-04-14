package org.opennaas.itests.router.chassis;

/*
 * #%L
 * OpenNaaS :: iTests :: Router
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.opennaas.itests.helpers.TestsConstants;
import org.opennaas.itests.router.helpers.ParamCreationHelper;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ChassisCapabilityIntegrationTest
{
	private final static Log	log					= LogFactory.getLog(ChassisCapabilityIntegrationTest.class);

	private final static String	RESOURCE_INFO_NAME	= "Chassis Test";

	protected IResource			routerResource;

	private boolean				isMock				= true;

	@SuppressWarnings("unused")
	@Inject
	private BundleContext		bundleContext;

	@Inject
	protected IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)", timeout = 20000)
	private BlueprintContainer	netconfService;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer	routerRepoService;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-router", "opennaas-router-driver-junos", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(), 
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Test
	@Ignore
	// FIXME this tests fails because of a vlan-tagging limitation describes at OPENNAAS-95 issue.
	public void testSetEncapsulationAction() throws CapabilityException, ProtocolException {

		int actionCount = 0;

		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));

		chassisCapability.setEncapsulation(ParamCreationHelper.newParamsInterfaceEthernetPort("fe-0/1/0", 13, 13),
				ProtocolIFType.LAYER_2_VLAN_USING_802_1Q);
		actionCount++;

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		assertEquals(actionCount, queue.size());

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		assertEquals(actionCount, queueResponse.getResponses().size());

		for (int i = 0; i < queueResponse.getResponses().size(); i++) {
			assertEquals(ActionResponse.STATUS.OK, queueResponse.getResponses().get(i).getStatus());
			for (Response response : queueResponse.getResponses().get(i).getResponses()) {
				assertEquals(Response.Status.OK, response.getStatus());
			}
		}

		assertEquals(ActionResponse.STATUS.OK, queueResponse.getPrepareResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getConfirmResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getRefreshResponse().getStatus());
		assertEquals(ActionResponse.STATUS.PENDING, queueResponse.getRestoreResponse().getStatus());

		assertTrue("Response should be ok", queueResponse.isOk());
	}

	@Test
	public void testChassisAction() throws CapabilityException, ProtocolException {
		log.info("TEST CHASSIS ACTIONS");

		int actionCount = 0;

		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));

		chassisCapability.createSubInterface(ParamCreationHelper.newParamsInterfaceEthernetPort("fe-0/1/0", 13, 13));
		actionCount++;

		chassisCapability.deleteSubInterface(ParamCreationHelper.newParamsInterfaceEthernetPort("fe-0/0/3", 13, 13));
		actionCount++;

		// // FIXME disabled as it fails (it is tested in testSetEncapsulationAction, now ignored)
		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETENCAPSULATION, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);
		// actionCount++;

		// setInterfaceDescriptionAction was moved to ipv4 capab
		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, newParamsInterfaceEthernetPort("fe-0/1/0",
		// 13));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);

		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, newParamsInterfaceLogicalPort("fe-0/1/0"));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);

		List<LogicalPort> lInterfaces = new ArrayList<LogicalPort>();
		lInterfaces.add(ParamCreationHelper.newParamsInterfaceEthernetPort("fe-0/1/0", 13, 13));
		chassisCapability.addInterfacesToLogicalRouter(ParamCreationHelper.newParamsLRWithInterface("cpe1"), lInterfaces);
		actionCount++;

		lInterfaces = new ArrayList<LogicalPort>();
		lInterfaces.add(ParamCreationHelper.newParamsInterfaceEthernetPort("fe-0/0/3", 13, 13));
		chassisCapability.removeInterfacesFromLogicalRouter(ParamCreationHelper.newParamsLRWithInterface("cpe2"), lInterfaces);
		actionCount++;

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		assertEquals(actionCount, queue.size());

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		assertEquals(actionCount, queueResponse.getResponses().size());

		for (int i = 0; i < queueResponse.getResponses().size(); i++) {
			assertEquals(ActionResponse.STATUS.OK, queueResponse.getResponses().get(i).getStatus());
			for (Response response : queueResponse.getResponses().get(i).getResponses()) {
				assertEquals(Response.Status.OK, response.getStatus());
			}
		}

		assertEquals(ActionResponse.STATUS.OK, queueResponse.getPrepareResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getConfirmResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getRefreshResponse().getStatus());
		assertEquals(ActionResponse.STATUS.PENDING, queueResponse.getRestoreResponse().getStatus());

		assertTrue("Response should be ok", queueResponse.isOk());

		queue = (List<IAction>) queueCapability.getActions();
		assertTrue("Queue should be empty", queue.isEmpty());
	}

	@Test
	public void UpDownActionTest() throws CapabilityException, ProtocolException {
		// Force to refresh the model

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		String str = "";
		ComputerSystem model = (ComputerSystem) routerResource.getModel();
		Assert.assertNotNull(model);
		for (LogicalDevice device : model.getLogicalDevices()) {
			if (device instanceof EthernetPort) {
				EthernetPort port = (EthernetPort) device;
				Assert.assertNotNull("OperationalStatus must be set", port.getOperationalStatus());

				str += "- EthernetPort: " + '\n';
				str += port.getName() + '.' + port.getPortNumber() + '\n';
				str += port.getOperationalStatus();
				str += '\n';
				for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint)
								protocolEndpoint;
						str += "ipv4: " + ipProtocol.getIPv4Address() + '\n';
						str += "ipv6: " + ipProtocol.getIPv6Address() + '\n';
					}
				}
			}
			else {
				str += "not searched device";
			}

		}
		log.info(str);
		String interfaceName = "fe-0/1/3";

		/* check model */
		LogicalDevice logicalDevice = null;
		try {
			logicalDevice = ParamCreationHelper.getLogicalDevice(interfaceName, (ComputerSystem) routerResource.getModel());
		} catch (Exception ex) {
			Assert.fail("LogicalDevice not found");
		}

		if (logicalDevice.getOperationalStatus() != OperationalStatus.OK) {
			Assert.fail("The test can't be executed because the needed interface is down");
		}

		/* send to change status */
		IChassisCapability chassisCapability = (IChassisCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.CHASSIS_CAPABILITY_TYPE));
		chassisCapability.downPhysicalInterface(ParamCreationHelper.newParamsConfigureStatus(interfaceName, OperationalStatus.STOPPED));

		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 1);
		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 0);

		if (!isMock) {
			checkOperationalStatus((ComputerSystem) routerResource.getModel(), interfaceName, OperationalStatus.STOPPED);
		}

		/* send to change status */
		chassisCapability.upPhysicalInterface(ParamCreationHelper.newParamsConfigureStatus(interfaceName, OperationalStatus.OK));

		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 1);
		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 0);

		if (!isMock) {
			checkOperationalStatus((ComputerSystem) routerResource.getModel(), interfaceName, OperationalStatus.OK);
		}
	}

	public void startResource() throws ResourceException, ProtocolException {

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor chassisCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.CHASSIS_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(chassisCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, routerResource.getResourceIdentifier().getId(), TestsConstants.RESOURCE_URI);

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

	@Before
	public void initBundle() throws Exception {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
		startResource();
	}

	@After
	public void stopBundle() throws Exception {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

	private void checkOperationalStatus(ComputerSystem model, String interfaceName, OperationalStatus status) {
		LogicalPort port = null;
		try {
			LogicalDevice port1 = ParamCreationHelper.getLogicalDevice(interfaceName, model);
			if (port1 instanceof LogicalPort)
				port = (LogicalPort) port1;
		} catch (Exception e) {
		}

		if (port == null)
			Assert.fail("Interface not found in model");
		Assert.assertTrue(port.getOperatingStatus().equals(status));
	}

}
