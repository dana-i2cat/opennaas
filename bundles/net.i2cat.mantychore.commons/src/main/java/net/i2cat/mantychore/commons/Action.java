package net.i2cat.mantychore.commons;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.commons.Response.Status;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Action {

	Logger					log			= LoggerFactory
														.getLogger(Action.class);

	private String			protocolId;
	private String			resourceId;
	protected List<Command>	commands	= new ArrayList<Command>();
	private Object			modelToUpdate;

	public ActionResponse execute(IProtocolSession protocol, Object params) throws ProtocolException, CommandException {
		ActionResponse actionResponse = new ActionResponse();

		log.info("executing commands for an action");

		for (Command command : commands) {
			log.info("initializing");
			command.initialize(params);
			try {
				log.info("sending...");
				Response response = sendCommandToProtocol(command, protocol);
				actionResponse.addResponse(response);

				if (response.getStatus().equals(Status.ERROR)) {
					// exit from the bucle, it is necessary
					break;
				}

			} catch (ProtocolException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				throw e;

			} catch (CommandException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				throw e;
			}
		}

		return actionResponse;

	}

	private Response sendCommandToProtocol(Command command, IProtocolSession protocol) throws ProtocolException, CommandException {
		log.info("sending and parsing message");
		Object messageResp = protocol.sendReceive(command.message());
		Response response = command.checkResponse(messageResp);
		// If it was not problems with the operation, we can update the model
		if (response.getStatus().equals(Response.Status.OK)) {
			command.parseResponse(messageResp, modelToUpdate);
		}
		return response;
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
