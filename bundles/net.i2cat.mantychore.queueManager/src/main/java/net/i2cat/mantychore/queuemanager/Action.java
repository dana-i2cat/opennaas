package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Action {

	Logger							log			= LoggerFactory
														.getLogger(Action.class);

	private String					protocolId;
	private String					resourceId;
	private List<Command>			commands	= new ArrayList<Command>();

	private Object					modelToUpdate;

	private ProtocolSessionContext	protocolSessionContext;

	public Action(ProtocolSessionContext protocolSessionContext) {
		this.protocolSessionContext = protocolSessionContext;

	}

	public void execute() throws ProtocolException {

		ProtocolNetconfWrapper protocolWrapper = new ProtocolNetconfWrapper();

		/* use pool for get protocol session */
		log.info("getting protocol session...");

		// TODO BUG, IT HAVE TO SPECIFY THE RESOURCE ID
		String sessionId = protocolWrapper.createProtocolSession(resourceId, protocolSessionContext);

		IProtocolSession protocol = protocolWrapper.getProtocolSession(sessionId);

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
		/* restore the connection */
		protocolWrapper.releaseProtocolSession(sessionId);
	}

	public void sendCommandToProtocol(Command command, IProtocolSession protocol) throws ProtocolException {
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
