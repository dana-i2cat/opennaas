package net.i2cat.mantychore.queuemanager.mock;

import net.i2cat.mantychore.queuemanager.Command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockCommand extends Command {
	static Logger	log	= LoggerFactory
								.getLogger(MockCommand.class);

	public MockCommand(String commandId) {
		super.commandId = commandId;
	}

	public void initialize() {
		log.info("Initializing command: " + commandId);
	}

	public Object message() {
		log.info("executing command: " + commandId);
		return "mock command execute";
	}

	public void parseResponse(Object updatedModel) {
		log.info("Initializing command: " + commandId);

	}

}
