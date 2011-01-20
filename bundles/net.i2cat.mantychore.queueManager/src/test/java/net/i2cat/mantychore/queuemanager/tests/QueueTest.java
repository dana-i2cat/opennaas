package net.i2cat.mantychore.queuemanager.tests;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.queuemanager.Action;
import net.i2cat.mantychore.queuemanager.Command;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.mantychore.queuemanager.QueueManagerFactory;
import net.i2cat.mantychore.queuemanager.mock.Interface;
import net.i2cat.mantychore.queuemanager.mock.MockCommand;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.protocolsessionmanager.ProtocolException;

public class QueueTest {

	static Logger	log	= LoggerFactory
								.getLogger(QueueTest.class);

	@Test
	public void firstActionTest() {
		String resourceId = "junos";
		String protocolId = "netconf";

		log.info("Calling queue factory");
		QueueManagerFactory queueFactory = new QueueManagerFactory();
		IQueueManagerService queueManager = queueFactory.createQueueManager(resourceId);

		log.info("Creating action");

		Action action = new Action();
		action.setResourceId(resourceId);
		action.setProtocolId(protocolId);

		List<Interface> interfaces = new ArrayList<Interface>();
		action.setModelToUpdate(interfaces);

		List<Command> commands = new ArrayList<Command>();

		log.info("Creating commands");
		Command command1 = new MockCommand();
		command1.setCommandId("prepare");
		commands.add(command1);

		Command command2 = new MockCommand();
		command2.setCommandId("command1");
		commands.add(command2);

		Command command3 = new MockCommand();
		command3.setCommandId("command2");
		commands.add(command3);
		action.setCommands(commands);

		Command command4 = new MockCommand();
		command4.setCommandId("commit");
		commands.add(command4);

		log.info("executing...");
		queueManager.queueAction(action);

		if (queueManager.getActions().size() != 1) {
			Assert.fail("Ups, the action disappeared");
		}

		if (queueManager.getActions().get(0).getCommands().size() != 2) {
			Assert.fail("Ups, the commands disappeared");
		}

		try {
			queueManager.execute();
		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

		for (Interface interf : interfaces) {
			log.info("interface");
			log.info(interf.getIpv4());
			log.info(interf.getIpv6());
			log.info("--------");

		}
	}

}
