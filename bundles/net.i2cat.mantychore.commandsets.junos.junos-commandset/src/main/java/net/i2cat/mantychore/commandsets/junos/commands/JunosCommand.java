package net.i2cat.mantychore.commandsets.junos.commands;

import java.util.HashMap;

import net.i2cat.mantychore.commandsets.junos.velocity.VelocityEngine;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.iaasframework.capabilities.commandset.AbstractCommandWithProtocol;
import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.protocol.ProtocolErrorMessage;
import com.iaasframework.capabilities.protocol.ProtocolResponseMessage;
import com.iaasframework.resources.core.message.ICapabilityMessage;

public abstract class JunosCommand extends AbstractCommandWithProtocol {
	private String					template	= "";

	private HashMap<String, String>	params		= new HashMap<String, String>();

	public JunosCommand(String commandID) {
		super(commandID);
		params = new HashMap<String, String>();
	}

	@Override
	public void executeCommand() throws CommandException {

		try {
			String command = prepareCommand();
			sendCommandToProtocol(command);
		} catch (CommandException ex) {
			ex.printStackTrace();
		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void initializeCommand(IResourceModel arg0) throws CommandException {
		// It is not necessary to fill
	}

	private String prepareCommand() throws ResourceNotFoundException,
			ParseErrorException, Exception {
		VelocityEngine velocityEngine = new VelocityEngine(template, params);
		return velocityEngine.mergeTemplate();

	}

	public void responseReceived(ICapabilityMessage responseMessage)
			throws CommandException {
		if (responseMessage instanceof ProtocolResponseMessage) {
			this.response = (ProtocolResponseMessage) responseMessage;
			this.requestEngineModel(false);
		} else if (responseMessage instanceof ProtocolErrorMessage) {
			this.errorMessage = (ProtocolErrorMessage) responseMessage;
			CommandException commandException = new CommandException(
					"Error executing command " + this.commandID,
					((ProtocolErrorMessage) responseMessage)
							.getProtocolException());
			commandException.setName(this.commandID);
			commandException.setCommand(this);
			this.sendCommandErrorResponse(commandException);
		}
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

	public void addParam(String key, String value) {
		this.params.put(key, value);
	}

}
