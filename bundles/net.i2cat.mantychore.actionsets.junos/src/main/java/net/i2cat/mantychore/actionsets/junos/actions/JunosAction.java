package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.List;
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

	public String prepareVelocityCommand(Object params, String template) throws ResourceNotFoundException,
			ParseErrorException, Exception {
		velocityEngine.setParam(params);
		velocityEngine.setTemplate(template);
		String command = velocityEngine.mergeTemplate();
		return command;
	}

	public String prepareVelocityCommand(Object params, String template, Map<String, Object> extraParams) throws ResourceNotFoundException,
			ParseErrorException, Exception {
		velocityEngine.setParam(params);
		velocityEngine.setTemplate(template);
		for (String name : extraParams.keySet())
			velocityEngine.addExtraParam(name, extraParams.get(name));
		String command = velocityEngine.mergeTemplate();
		return command;
	}

	public void validateAction(ActionResponse actionResponse) throws ActionException {

		for (Response response : actionResponse.getResponses()) {
			if (response.getStatus() == Response.Status.ERROR) {
				actionResponse.setStatus(ActionResponse.STATUS.ERROR);
				return;
			}
		}

	}

	public Response sendCommandToProtocol(JunosCommand command, IProtocolSession
				generalProtocol) throws ProtocolException,
			ActionException {
		// FIXME PARSE TO NETCONFPROTOCOL SESSION
		NetconfProtocolSession protocol = (NetconfProtocolSession) generalProtocol;
		Object messageResp = protocol.sendReceive(command.message());

		Response response = command.checkResponse(messageResp);

		if (response.getStatus().equals(Response.Status.OK))
			parseResponse(messageResp, modelToUpdate);

		return response;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		ActionResponse actionResponse = new ActionResponse();
		try {

			// TODO WHY THE PROTOCOL NAME, WE ALWAYS USE A NETCONFPROTOCOLSESSION PROTOCOL
			NetconfProtocolSession protocol = (NetconfProtocolSession) protocolSessionManager.obtainSessionByProtocol(protocolName, false);
			/* call commands */
			prepareMessage();

			actionResponse.setStatus(ActionResponse.STATUS.OK);
			actionResponse.setActionID(this.getActionID());

			executeListCommand(actionResponse, protocol);

			// protocolSessionManager.releaseSession(protocol);

			return actionResponse;

		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	private String concatenateErrors(List<String> errors) {
		String listErrors = "";
		for (String error : errors) {
			listErrors += "- " + error + '\n';
		}
		return listErrors;
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

	@Override
	public abstract boolean checkParams(Object params) throws ActionException;

	public abstract void prepareMessage() throws ActionException;
}
