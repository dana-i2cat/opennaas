package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.queuemanager.mock.MockProtocolWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.ProtocolException;

public class Action {

	Logger					log			= LoggerFactory
												.getLogger(Action.class);

	private String			protocolId;
	private String			resourceId;
	private List<Command>	commands	= new ArrayList<Command>();

	private Object			modelToUpdate;

	public Action() {

	}

	public void execute() throws ProtocolException {

		MockProtocolWrapper protocolWrapper = new MockProtocolWrapper();
		IProtocolSession protocol = protocolWrapper.getProtocolSession(resourceId, protocolId);

		for (Command command : commands) {
			command.initialize();
			try {
				sendCommandToProtocol(command, protocol);
			} catch (ProtocolException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				throw e;

			}
		}
	}

	public void sendCommandToProtocol(Command command, IProtocolSession protocol) throws ProtocolException {
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
