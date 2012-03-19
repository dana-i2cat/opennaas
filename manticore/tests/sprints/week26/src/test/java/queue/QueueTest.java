package queue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.helpers.MockActionFactory;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.swissbox.framework.ServiceLookup.getService;

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
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class QueueTest
{
	/* Note that the tests in this class have to run in isolation;
	 * this is because the initialization code is creating a new queue
	 * before each test runs.
	 */


	private final static Log		log				= LogFactory.getLog(QueueTest.class);

	private final static String		resourceID		= "junosResource";
	private MockResource			mockResource;
	private ICapability				queueCapability;
	private IQueueManagerService	queueManagerService;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	private BundleContext			bundleContext;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-cim", "opennaas-netconf"),
					   includeTestHelper(),
					   includeSwissboxFramework(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	public void initBundles() throws ProtocolException {
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
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor(
				QueueManager.QUEUE, "queue"));

		resourceDescriptor.setProperties(properties);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceDescriptor.setId(resourceID);

		mockResource.setResourceDescriptor(resourceDescriptor);

		protocolManager.getProtocolSessionManagerWithContext(resourceID, ResourceHelper.newSessionContextNetconf());

		log.info("INFO: Initialized!");

	}

	@Before
	public void before() throws ProtocolException, CapabilityException {
		initBundles();
		log.info("INFO: Before test, getting queue...");
		queueCapability = queueManagerFactory.create(mockResource);

		/* The queue manager factory registers the new queue manager
		 * as a service. Hence we cannot obtain this reference through
		 * injection.
		 */
		queueManagerService =
			getService(bundleContext, IQueueManagerService.class, 20000,
					   String.format("(capability=queue)(capability.name=%s)",
									 resourceID));
	}

	@After
	public void after() {
		log.info("INFO: After test, cleaning queue...");
		queueManagerService.empty();
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

		queueCapability.sendMessage(QueueConstants.MODIFY, newQueueModifyParams());
		Assert.assertTrue(queueManagerService.getActions().size() == 2);
	}

	private Object newQueueModifyParams() {
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
	 */
	@Test
	public void ResponseReportTest() throws CapabilityException, InterruptedException {
		queueManagerService.queueAction(MockActionFactory.newMockActionDiffsCommandOks("action1"));
		queueManagerService.queueAction(MockActionFactory.newMockActionAnError("action2"));
		queueManagerService.queueAction(MockActionFactory.newMockActionVariousError("action3"));

		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);

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
