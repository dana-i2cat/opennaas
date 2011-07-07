package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.Map;

import net.i2cat.mantychore.commandsets.junos.commands.JunosCommand;
import net.i2cat.mantychore.commandsets.junos.velocity.VelocityEngine;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSession;
import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public abstract class JunosAction extends Action {
	VelocityEngine		velocityEngine;
	String				template		= null;
	protected String	protocolName	= null;
	protected String	velocityMessage;

	public JunosAction() {
		velocityEngine = new VelocityEngine();
	}

	public String prepareVelocityCommand(Object params, String template, Map<String, Object> extraParams) throws ResourceNotFoundException,
			ParseErrorException, Exception {
		velocityEngine.setParam(params);
		velocityEngine.setTemplate(template);
		if (extraParams != null) {
			for (String name : extraParams.keySet())
				velocityEngine.addExtraParam(name, extraParams.get(name));
		}
		String command = velocityEngine.mergeTemplate();
		return command;
	}

	public Response sendCommandToProtocol(JunosCommand command, IProtocolSession
				generalProtocol) throws ProtocolException,
			ActionException {
		// FIXME PARSE TO NETCONFPROTOCOL SESSION
		NetconfProtocolSession protocol = (NetconfProtocolSession) generalProtocol;
		Object messageResp = protocol.sendReceive(command.message());
		Response response = command.checkResponse(messageResp);
		validateResponse(response);
		parseResponse(messageResp, modelToUpdate);

		return response;
	}

	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			
			//TODO WHY THE PROTOCOL NAME, WE ALWAYS USE A NETCONFPROTOCOLSESSION PROTOCOL
			NetconfProtocolSession protocol = (NetconfProtocolSession) protocolSessionManager.obtainSessionByProtocol(protocolName, false);
			/* call commands */
			prepareMessage();
			ActionResponse actionResponse = new ActionResponse();
			executeListCommand(actionResponse, protocol);
			actionResponse.setActionID(this.getActionID());

			// protocolSessionManager.releaseSession(protocol);
			return actionResponse;

		} catch (ProtocolException e) {
			throw new ActionException(e);
		}

	}

	private void validateResponse(Response response) throws ActionException {

		if (response.getErrors() != null && response.getErrors().size() > 0) {
			String listErrors = new String();
			for (String error : response.getErrors())
				listErrors += "-" + error + "\n";
			ActionException actionException = new ActionException(listErrors);
			throw actionException;
		}
		if (!response.getStatus().equals(Response.Status.OK))
			throw new ActionException();

	}

	public String getVelocityMessage() {
		return velocityMessage;
	}

	public void setVelocityMessage(String velocityMessage) {
		this.velocityMessage = velocityMessage;
	}

	public String getTemplate() {
		return template;
	}

	protected void setTemplate(String template) {
		this.template = template;
	}

	public abstract void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException;

	public abstract void parseResponse(Object responseMessage, Object model) throws ActionException;

	public abstract boolean checkParams(Object params) throws ActionException;

	public abstract void prepareMessage() throws ActionException;
}
