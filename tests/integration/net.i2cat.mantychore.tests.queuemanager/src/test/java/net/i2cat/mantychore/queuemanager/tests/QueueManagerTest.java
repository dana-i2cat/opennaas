package net.i2cat.mantychore.queuemanager.tests;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4TestRunner.class)
public class QueueManagerTest extends AbstractIntegrationTest {

	static Logger			log				= LoggerFactory
													.getLogger(QueueManagerTest.class);

	IQueueManagerService	queueManager;
	String					deviceID		= "junos";

	@Inject
	BundleContext			bundleContext	= null;

	@Configuration
	public static Option[] configure() {
		return IntegrationTestsHelper.getMantychoreTestOptions();
	}

	/* initialize client */

	private void prepareQueueManagerTest() {
		try {
			log.info("Starting the test");

			/* get queueManager as a service */
			log.info("Getting queue manager factory...");
			IQueueManagerFactory queueManagerFactory = getOsgiService(IQueueManagerFactory.class);
			log.info("Getting queue manager...");
			queueManager = queueManagerFactory.createQueueManager(deviceID);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
	}

	@Test
	public void testListAction() {
		log.info("This is running inside Equinox. With all configuration set up like you specified. ");
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		/* list bundles */
		IntegrationTestsHelper.listBundles(bundleContext);

		/* prepare queue manager test */
		prepareQueueManagerTest();

		Action action = newAction();
		queueManager.queueAction(action, newSessionContextNetconf());
		Action action2 = newAction();
		queueManager.queueAction(action2, newSessionContextNetconf());
		Action action3 = newAction();
		queueManager.queueAction(action3, newSessionContextNetconf());
		Action action4 = newAction();
		queueManager.queueAction(action4, newSessionContextNetconf());
		Action action5 = newAction();
		queueManager.queueAction(action5, newSessionContextNetconf());

		/* ------------------------ */

		List<Action> actions = queueManager.getActions();
		if (actions == null || actions.size() != 5)
			Assert.fail("the queuemanager does not include any action");

		try {
			List<ActionResponse> responses = queueManager.execute();
			for (ActionResponse actionResponse : responses) {
				for (Response response : actionResponse.getResponses()) {
					log.info("Response messages" + '\n');
					log.info("MESSAGE: " + response.getSentMessage() + '\n');
					log.info("STATUS: " + response.getStatus().name() + '\n');
					log.info("ERRORS" + '\n');
					for (String error : response.getErrors())
						log.info("error: " + error + '\n');
				}
			}
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		} catch (CommandException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

		queueManager.empty();
		actions = queueManager.getActions();

		if (actions != null && actions.size() != 0)
			Assert.fail("the queuemanager is not empty");
	}

	private Action newAction() {
		Action action = new Action();

		Command command = new MockCommand();

		List<Command> commands = new ArrayList<Command>();
		commands.add(command);
		action.setCommands(commands);

		return action;
	}

	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@host.net:2212/mocksubsystem");

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		// ADDED
		return protocolSessionContext;

	}

	class MockCommand extends Command {
		Query	query	= QueryFactory.newKeepAlive();

		@Override
		public void initialize() {
			log.info("Initialization");

		}

		@Override
		public Object message() {
			log.info("sending a message");

			return query;
		}

		@Override
		public void parseResponse(Object arg0, Object arg1) {
			log.info("It is parsed the message");

		}

		@Override
		public Response checkResponse(Object arg0) {
			// TODO Auto-generated method stub
			return Response.okResponse(query.toXML());
		}
	}

}
