package org.opennaas.itests.router.staticroute;

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

import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.staticroute.StaticRouteCapability;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.opennaas.itests.helpers.TestsConstants;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * 
 * @author Jordi Puig
 * @author Adrian Rosello
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class StaticRouteIntegrationTest {

	protected static final String	RESOURCE_INFO_NAME	= "Static Route Test";

	protected ICapability			iStaticRouteCapability;
	protected IResource				routerResource;

	@Inject
	protected IResourceManager		resourceManager;

	@Inject
	protected IProtocolManager		protocolManager;

	@Inject
	protected BundleContext			bundleContext;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)", timeout = 20000)
	private BlueprintContainer		netconfService;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer		routerRepoService;

	private static final Log		log					= LogFactory
																.getLog(StaticRouteIntegrationTest.class);

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
	/**
	 * Test to check if capability is available from OSGi.
	 */
	public void isCapabilityAccessibleFromResource()
			throws ResourceException, ProtocolException
	{
		Assert.assertFalse(routerResource.getCapabilities().isEmpty());
		Assert.assertNotNull(routerResource.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE)));
		Assert.assertNotNull(routerResource.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.STATIC_ROUTE_CAPABILITY_TYPE)));
	}

	/**
	 * Test to check create static route method
	 */
	@Test
	public void createStaticRouteTest()
			throws ProtocolException, ResourceException {

		StaticRouteCapability staticRouteCapability = (StaticRouteCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.STATIC_ROUTE_CAPABILITY_TYPE));
		staticRouteCapability.createStaticRoute("0.0.0.0/0", "192.168.1.1", false, StaticRouteCapability.PREFERENCE_DEFAULT_VALUE);

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		staticRouteCapability.createStaticRoute("0.0.0.0", "0.0.0.0", "192.168.1.1", "false");

		queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		staticRouteCapability.createStaticRoute("45:34fa:12::4e/64", "45:34fa:12::4e:12", false, StaticRouteCapability.PREFERENCE_DEFAULT_VALUE);

		queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		staticRouteCapability.createStaticRoute("45:34fa:12::4e", "64", "45:34fa:12::4e:12", "false");

		queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

	}

	/**
	 * Test to check create static route method
	 */
	@Test
	public void deleteStaticRouteTest()
			throws ProtocolException, ResourceException {

		StaticRouteCapability staticRouteCapability = (StaticRouteCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.STATIC_ROUTE_CAPABILITY_TYPE));
		staticRouteCapability.deleteStaticRoute("0.0.0.0/0", "192.168.1.1");

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		staticRouteCapability.deleteStaticRoute("0.0.0.0", "0.0.0.0", "192.168.1.1");

		queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		staticRouteCapability.deleteStaticRoute("45:34fa:12::4e/64", "45:34fa:12::4e:12");

		queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		staticRouteCapability.deleteStaticRoute("45:34fa:12::4e", "64", "45:34fa:12::4e:12");

		queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		queueResponse = queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

	}

	@Before
	public void initBundles() throws ResourceException, ProtocolException {
		InitializerTestHelper.removeResources(resourceManager);
		startResource();
		log.info("INFO: Initialized!");

	}

	/**
	 * Start router resource with 2 capabilities -> staticroute & queue
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void startResource() throws ResourceException, ProtocolException {
		// Add Static Route Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor staticrouteCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(
				TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.STATIC_ROUTE_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(staticrouteCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		// Create resource
		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, routerResource.getResourceIdentifier().getId(), TestsConstants.RESOURCE_URI);

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

	@After
	public void stopBundle() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

}
