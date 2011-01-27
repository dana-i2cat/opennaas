package net.i2cat.mantychore.commons;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Action {

	Logger					log			= LoggerFactory
														.getLogger(Action.class);

	private String			protocolId;
	private String			resourceId;
	private List<Command>	commands	= new ArrayList<Command>();

	private Object			modelToUpdate;

	public void execute(IProtocolSession protocol) throws ProtocolException {

		log.info("executing commands for an action");
		for (Command command : commands) {
			log.info("initializing");
			command.initialize();
			try {
				log.info("sending...");
				sendCommandToProtocol(command, protocol);
			} catch (ProtocolException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				throw e;

			}
		}

	}

	private void sendCommandToProtocol(Command command, IProtocolSession protocol) throws ProtocolException {
		log.info("sending and parsing message");
		command.parseResponse(protocol.sendReceive(command.message()), modelToUpdate);
	}

	public String getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public Object getModelToUpdate() {
		return modelToUpdate;
	}

	public void setModelToUpdate(Object modelToUpdate) {
		this.modelToUpdate = modelToUpdate;
	}

}
