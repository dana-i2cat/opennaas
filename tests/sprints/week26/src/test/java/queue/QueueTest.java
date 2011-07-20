package queue;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.IntegrationTestsHelper;
import helpers.ProtocolSessionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.resources.helpers.MockActionFactory;
import net.i2cat.nexus.resources.helpers.MockResource;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.queue.ModifyParams;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

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
public class QueueTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log				log				= LogFactory
													.getLog(QueueTest.class);

	MockResource			mockResource;
	String					resourceID		= "junosResource";
	// QueueManager queueManagerService;
	ICapability				queueCapability;
	IQueueManagerService	queueManagerService;

	@Inject
	BundleContext			bundleContext	= null;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	public void initBundles() throws ProtocolException {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

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

		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 20000);
		protocolManager.getProtocolSessionManagerWithContext(resourceID, ProtocolSessionHelper.newSessionContextNetconf());

		log.info("INFO: Initialized!");

	}

	// FIXME Before and After does not work with linux

	@Before
	public void before() throws ProtocolException, CapabilityException {
		initBundles();
		log.info("INFO: Before test, getting queue...");
		ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 20000);
		queueCapability = queueManagerFactory.create(mockResource);
		queueManagerService = getOsgiService(IQueueManagerService.class,
				"(capability=queue)(capability.name=" + resourceID + ")", 20000);

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
	public void ModifyQueueTest() {
		// log.info("INFO: Remove actions");
		// IAction action = new MockAction();
		// action.setActionID("mockAction");

		queueManagerService.queueAction(MockActionFactory.newMockActionAnError("action1"));
		queueManagerService.queueAction(MockActionFactory.newMockActionDiffsCommandOks("action2"));
		queueManagerService.queueAction(MockActionFactory.newMockActionOK("action3"));

		try {
			queueCapability.sendMessage(QueueConstants.MODIFY, newQueueModifyParams());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
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
	public void ResponseReportTest() {
		queueManagerService.queueAction(MockActionFactory.newMockActionDiffsCommandOks("action1"));
		queueManagerService.queueAction(MockActionFactory.newMockActionAnError("action2"));
		queueManagerService.queueAction(MockActionFactory.newMockActionVariousError("action3"));

		QueueResponse queueResponse = null;
		try {
			queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		} catch (CapabilityException e) {
			Assert.fail(e.getMessage());
		}

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
