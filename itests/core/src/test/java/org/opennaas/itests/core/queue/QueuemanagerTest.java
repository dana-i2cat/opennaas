package org.opennaas.itests.core.queue;

/*
 * #%L
 * OpenNaaS :: iTests :: Core
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
import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockAction;
import org.opennaas.core.resources.mock.MockActionExceptionOnExecute;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.swissbox.tracker.ServiceLookup;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class QueuemanagerTest
{
	private final static Log		log			= LogFactory.getLog(QueuemanagerTest.class);

	private final String			resourceID	= "junosResource";
	private MockResource			mockResource;
	private ICapability				queueCapability;
	private IQueueManagerCapability	queueManagerCapability;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer		routerService;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)", timeout = 20000)
	private BlueprintContainer		queueService;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-router", "opennaas-router-driver-junos"),
				OpennaasExamOptions.includeSwissboxFramework(),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(), 
				OpennaasExamOptions.keepRuntimeFolder());
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");

		return protocolSessionContext;
	}

	@Before
	public void before() throws ProtocolException, IncorrectLifecycleStateException, ResourceException, CorruptStateException {
		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		// will not be the same that mockResource but will do the trick
		resourceManager.createResource(resourceDescriptor);

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("INFO: Before test, getting queue...");
		queueCapability = queueManagerFactory.create(mockResource);
		((ICapabilityLifecycle) queueCapability).initialize();

		queueManagerCapability = ServiceLookup.getService(bundleContext, IQueueManagerCapability.class, 20000,
				"(capability=queue)(capability.name=" + mockResource.getResourceId() + ")");
	}

	@After
	public void after() throws ResourceException {
		log.info("INFO: After test, cleaning queue...");
		queueManagerCapability.clear();
		resourceManager.destroyAllResources();
	}

	@Test
	public void removeAction() throws CapabilityException {
		log.info("INFO: Remove actions");
		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerCapability.queueAction(action);
		Assert.assertTrue(queueManagerCapability.getActions().size() == 1);
		queueManagerCapability.clear();
		Assert.assertTrue(queueManagerCapability.getActions().size() == 0);
		log.info("INFO: OK!");
	}

	@Test
	public void executeAction() throws ProtocolException, CapabilityException {
		log.info("INFO: Execute actions");

		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerCapability.queueAction(action);
		Assert.assertTrue(queueManagerCapability.getActions().size() == 1);
		queueManagerCapability.execute();
		Assert.assertTrue(queueManagerCapability.getActions().size() == 0);
		log.info("INFO: OK!");
	}

	@Test
	public void executeActionThatThrowsException() throws ProtocolException, CapabilityException {
		log.info("INFO: Execute actions");

		IAction action = new MockActionExceptionOnExecute();
		action.setActionID("mockAction");

		queueManagerCapability.queueAction(action);
		Assert.assertEquals(1, queueManagerCapability.getActions().size());
		QueueResponse response = queueManagerCapability.execute();
		Assert.assertFalse(response.isOk());
		Assert.assertFalse(response.getResponses().isEmpty());
		Assert.assertEquals(STATUS.ERROR, response.getResponses().get(0).getStatus());
		Assert.assertNotNull("Action information should contain the error message", response.getResponses().get(0).getInformation());
		Assert.assertFalse("Restore should be executed", STATUS.PENDING.equals(response.getRestoreResponse().getStatus()));

		log.info("INFO: OK!");
	}

	@Test
	public void listActions() throws CapabilityException {
		log.info("INFO: List actions");

		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerCapability.queueAction(action);
		Assert.assertTrue(queueManagerCapability.getActions().size() == 1);

		for (IAction act : queueManagerCapability.getActions()) {
			log.info("INFO: action id=" + act.getActionID());
		}

		log.info("INFO: OK!");
	}

}
