package org.opennaas.extensions.router.junos.actionssets.actions;

import java.util.List;
import java.util.Map;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.junos.commandsets.commands.JunosCommand;
import org.opennaas.extensions.router.junos.commandsets.velocity.VelocityEngine;

public abstract class JunosAction extends Action {
	protected VelocityEngine	velocityEngine;
	protected String			template		= null;
	protected String			protocolName	= null;
	protected String			velocityMessage;

	public JunosAction() {
		velocityEngine = new VelocityEngine();
	}

	public String prepareVelocityCommand(Object params, String template) throws ResourceNotFoundException,
			ParseErrorException, Exception {
		return prepareVelocityCommand(params, template, null);
	}

	public String prepareVelocityCommand(Object params, String template, Map<String, Object> extraParams) throws ResourceNotFoundException,
			ParseErrorException, Exception {
		velocityEngine.setParam(params);
		velocityEngine.setTemplate(template);
		if (extraParams != null) {
			for (String name : extraParams.keySet())
				velocityEngine.addExtraParam(name, extraParams.get(name));
		}
		return velocityEngine.mergeTemplate();
	}

	public void validateAction(ActionResponse actionResponse) throws ActionException {

		for (Response response : actionResponse.getResponses()) {
			if (response.getStatus() == Response.Status.ERROR) {
				actionResponse.setStatus(ActionResponse.STATUS.ERROR);
				actionResponse.setInformation(concatenateErrors(response.getErrors()));
				return;
			}
		}
	}

	public Response sendCommandToProtocol(JunosCommand command, IProtocolSession
			generalProtocol) throws ProtocolException,
			ActionException {
		IProtocolSession protocol = generalProtocol;
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
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol(protocolName, false);
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
