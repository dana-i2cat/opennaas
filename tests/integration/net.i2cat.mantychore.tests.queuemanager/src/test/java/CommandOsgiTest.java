import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.mantychore.queuemanager.Action;
import net.i2cat.mantychore.queuemanager.Command;
import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.PAXHelper;

@RunWith(JUnit4TestRunner.class)
public class CommandOsgiTest extends AbstractIntegrationTest {

	static Logger			log				= LoggerFactory
													.getLogger(CommandOsgiTest.class);

	IQueueManagerService	queueManager;
	String					deviceID		= "junos";

	@Inject
	BundleContext			bundleContext	= null;

	@Configuration
	public static Option[] configure() {
		return PAXHelper.newQueueManagerTest();
	}

	@Test
	public void ListBundles() {
		log.info("This is running inside Felix. With all configuration set up like you specified. ");
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		listBundles(bundleContext);

		/* initialize client */

	}

	public static void listBundles(BundleContext bundleContext) {
		Bundle b = null;
		for (int i = 0; i < bundleContext.getBundles().length; i++) {
			b = bundleContext.getBundles()[i];
			System.out.println(b.toString() + " : " + getStateString(b.getState()));
			if (getStateString(b.getState()).equals("INSTALLED")) {
				try {
					b.start();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private static String getStateString(int value) {
		if (value == Bundle.ACTIVE) {
			return "ACTIVE";
		} else if (value == Bundle.INSTALLED) {
			return "INSTALLED";
		} else if (value == Bundle.RESOLVED) {
			return "RESOLVED";
		} else if (value == Bundle.UNINSTALLED) {
			return "UNINSTALLED";
		}

		return "UNKNOWN";
	}

	/* initialize client */

	private void prepareQueueManagerTest() {
		try {
			log.info("Starting the test");

			/* get queueManager as a service */
			ServiceReference serviceReference = bundleContext.getServiceReference(IQueueManagerFactory.class.getName());
			log.info("Getting queue manager factory...");
			IQueueManagerFactory queueManagerFactory = (IQueueManagerFactory) bundleContext.getService(serviceReference);

			log.info("Getting queue manager factory...");
			queueManager = queueManagerFactory.createQueueManager(deviceID);
			log.info("Getting queue manager factory...");

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail();
		}
	}

	public void TestFirstAction() {

		prepareQueueManagerTest();

		Action action = newAction();

		queueManager.queueAction(action);

		List<Action> actions = queueManager.getActions();
		if (actions == null || actions.size() != 1)
			Assert.fail("the queuemanager does not include any action");

		try {
			queueManager.execute();
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

		queueManager.empty();

		if (actions == null || actions.size() != 0)
			Assert.fail("the queuemanager does not empty fine");

	}

	public void testListAction() {
		prepareQueueManagerTest();

		Action action = newAction();
		queueManager.queueAction(action);
		Action action2 = newAction();
		queueManager.queueAction(action2);
		Action action3 = newAction();
		queueManager.queueAction(action3);
		Action action4 = newAction();
		queueManager.queueAction(action4);
		Action action5 = newAction();
		queueManager.queueAction(action5);

		/* ------------------------ */

		List<Action> actions = queueManager.getActions();
		if (actions == null || actions.size() != 5)
			Assert.fail("the queuemanager does not include any action");

		try {
			queueManager.execute();
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

		queueManager.empty();

		if (actions == null || actions.size() != 0)
			Assert.fail("the queuemanager does not empty fine");

	}

	private Action newAction() {
		Action action = new Action(newSessionContextNetconf());

		Command command = new MockCommand();

		List<Command> commands = new ArrayList<Command>();
		commands.add(command);
		action.setCommands(commands);

		return action;
	}

	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://foo:bar@foo:22/netconf");
		// ADDED
		return protocolSessionContext;

	}

	class MockCommand extends Command {

		@Override
		public void initialize() {
			log.info("Initialization");

		}

		@Override
		public Object message() {
			log.info("sending a message");
			Query query = QueryFactory.newKeepAlive();
			return query;
		}

		@Override
		public void parseResponse(Object arg0, Object arg1) {
			log.info("It is parsed the message");

		}
	}

}
