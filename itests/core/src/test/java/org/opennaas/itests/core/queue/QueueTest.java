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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.mock.MockActionFactory;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.queuemanager.QueueManager;
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

/**
 * Tests new queue operations. In the sprint for the week 26, it is planned to add new features in the queue.
 * 
 * tasks:
 * 
 * 1.- A queue have to remove elements from its queue list
 * 
 * 2.- The queue has to implement a method to report responses from a list of actions
 * 
 * @author Carlos Báez Ruiz
 * 
 *         jira ticket : http://jira.i2cat.net:8080/browse/MANTYCHORE-185
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class QueueTest
{
	/*
	 * Note that the tests in this class have to run in isolation; this is because the initialization code is creating a new queue before each test
	 * runs.
	 */

	private final static Log		log				= LogFactory.getLog(QueueTest.class);

	private final static String		resourceName	= "junosResource";
	private String					resourceID;
	private MockResource			mockResource;
	private IQueueManagerCapability	queueCapability;
	private IQueueManagerCapability	queueManagerService;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer		routerService;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)", timeout = 20000)
	private BlueprintContainer		queueService;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	private BundleContext			bundleContext;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-cim", "opennaas-protocol-netconf", "opennaas-router", "opennaas-router-driver-junos",
						"itests-helpers"),
				OpennaasExamOptions.includeSwissboxFramework(),
				OpennaasExamOptions.noConsole(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	public void initBundles() throws ProtocolException, ResourceException {
		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		/* add queue capability */
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor(QueueManager.QUEUE, "queue"));

		resourceDescriptor.setProperties(properties);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		Information info = new Information();
		info.setName(resourceName);
		info.setType("router");
		resourceDescriptor.setInformation(info);

		mockResource.setResourceDescriptor(resourceDescriptor);

		// will not be the same that mockResource but will do the trick
		IResource resource = resourceManager.createResource(resourceDescriptor);
		resourceID = resource.getResourceIdentifier().getId();

		protocolManager.getProtocolSessionManagerWithContext(resourceID, ResourceHelper.newSessionContextNetconf());

		log.info("INFO: Initialized!");
	}

	@Before
	public void before() throws ProtocolException, IncorrectLifecycleStateException, ResourceException, CorruptStateException {
		initBundles();
		log.info("INFO: Before test, getting queue...");

		queueCapability = (IQueueManagerCapability) queueManagerFactory.create(mockResource);

		/*
		 * initialize() registers the new queue manager as a service. Hence we cannot obtain this reference through injection.
		 */
		((ICapabilityLifecycle) queueCapability).initialize();

		queueManagerService =
				ServiceLookup.getService(bundleContext, IQueueManagerCapability.class, 20000,
						String.format("(capability=queue)(capability.name=%s)",
								resourceID));
	}

	@After
	public void after() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException {
		log.info("INFO: After test, cleaning queue...");
		((ICapabilityLifecycle) queueCapability).shutdown();
		queueManagerService.clear();
		resourceManager.destroyAllResources();
	}

	/**
	 * A queue have to be new operations. In this sprint, it have to implement the remove operation to remove actions from the queue.
	 * 
	 * Estimation: 3 hours
	 * 
	 * tasks:
	 * 
	 * 1.- Add unitary tests,
	 * 
	 * 2.- Implement operation in the queue
	 */
	@Test
	public void ModifyQueueTest() throws Exception {
		// log.info("INFO: Remove actions");
		// IAction action = new MockAction();
		// action.setActionID("mockAction");
		queueManagerService.queueAction(MockActionFactory.newMockActionAnError("action1"));
		queueManagerService.queueAction(MockActionFactory.newMockActionDiffsCommandOks("action2"));
		queueManagerService.queueAction(MockActionFactory.newMockActionOK("action3"));

		queueCapability.modify(newQueueModifyParams());
		Assert.assertTrue(queueManagerService.getActions().size() == 2);
	}

	private ModifyParams newQueueModifyParams() {
		ModifyParams modifyParams = new ModifyParams();
		modifyParams.setPosAction(1);
		modifyParams.setQueueOper(ModifyParams.Operations.REMOVE);
		return modifyParams;
	}

	/**
	 * A queue have to be new operations. In this sprint, it have to implement the remove operation to remove actions from the queue.
	 * 
	 * Estimation: 5
	 * 
	 * hours tasks:
	 * 
	 * 1.- Add unitary test
	 * 
	 * 2.- Add necessary refactoring to add new information in the queue
	 * 
	 * @throws ProtocolException
	 */
	@Test
	public void ResponseReportTest() throws CapabilityException, InterruptedException, ProtocolException {
		queueManagerService.queueAction(MockActionFactory.newMockActionDiffsCommandOks("action1"));
		queueManagerService.queueAction(MockActionFactory.newMockActionAnError("action2"));
		queueManagerService.queueAction(MockActionFactory.newMockActionVariousError("action3"));

		QueueResponse queueResponse = queueCapability.execute();

		/* check prepare action */
		ActionResponse prepareResponse = queueResponse.getPrepareResponse();
		Assert.assertTrue(prepareResponse.getStatus() == ActionResponse.STATUS.OK);
		Assert.assertTrue(queueResponse.getResponses().size() == 3);

		ActionResponse actionResponse = queueResponse.getResponses().get(0);
		for (Response response : actionResponse.getResponses()) {
			Assert.assertTrue(response.getStatus() == Response.Status.OK);
			Assert.assertNotNull(response.getSentMessage() != null);
		}

		ActionResponse actionResponseAnError = queueResponse.getResponses().get(1);
		/* check message with error */

		Assert.assertTrue(actionResponseAnError.getStatus() == ActionResponse.STATUS.ERROR);
		Assert.assertTrue(actionResponseAnError.getResponses().size() == 1);

		ActionResponse actionResponsePending = queueResponse.getResponses().get(2);
		Assert.assertTrue(actionResponsePending.getStatus() == ActionResponse.STATUS.PENDING);

		/* confirm response */
		ActionResponse confirmResponse = queueResponse.getConfirmResponse();
		Assert.assertTrue(confirmResponse.getStatus() == ActionResponse.STATUS.PENDING);

		/* restore response */
		ActionResponse restoreResponse = queueResponse.getRestoreResponse();
		Assert.assertTrue(restoreResponse.getStatus() == ActionResponse.STATUS.OK);
	}
}
