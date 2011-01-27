package net.i2cat.mantychore.queuemanager.mock;

import java.util.List;

import net.i2cat.mantychore.commons.Command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockCommand extends Command {
	static Logger	log	= LoggerFactory
								.getLogger(MockCommand.class);

	public void initialize() {
		log.info("Initializing command: " + commandId);
	}

	public Object message() {
		log.info("executing command: " + commandId);
		return "mock command execute";
	}

	public void parseResponse(Object response, Object model) {
		log.info("parsing command: " + commandId);

		List<Interface> interfaces = (List<Interface>) model;
		Interface interf1 = new Interface();
		interf1.setIpv4("192.168.1.2");
		interf1.setIpv6("ff:ff::ff");
		interfaces.add(interf1);
		Interface interf2 = new Interface();
		interf2.setIpv4("192.168.2.2");
		interf2.setIpv6("ff:ff::22");
		interfaces.add(interf2);

	}

}
