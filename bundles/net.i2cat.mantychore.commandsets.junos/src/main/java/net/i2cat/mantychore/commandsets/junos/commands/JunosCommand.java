package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.velocity.VelocityEngine;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.AbstractCommandWithProtocol;
import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.protocol.api.ProtocolErrorMessage;
import com.iaasframework.capabilities.protocol.api.ProtocolResponseMessage;
import com.iaasframework.resources.core.message.ICapabilityMessage;

public abstract class JunosCommand extends AbstractCommandWithProtocol {
	protected String			template	= "";

	private Object				params		= "";

	protected DigesterEngine[]	digEngines;

	/** logger **/
	Logger						log			= LoggerFactory
														.getLogger(JunosCommand.class);

	protected JunosCommand(String commandID, String template, DigesterEngine[] digEngines) {
		super(commandID);
		this.template = template;
		this.digEngines = digEngines;

	}

	// initCommand will be used to pass arguments

	@Override
	public void executeCommand() throws CommandException {

		try {

			String command = prepareCommand();
			sendCommandToProtocol(command);

		} catch (CommandException ex) {
			log.error(ex.getMessage());
		} catch (ResourceNotFoundException e) {
			log.error(e.getMessage());
		} catch (ParseErrorException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	private String prepareCommand() throws ResourceNotFoundException,
			ParseErrorException, Exception {

		VelocityEngine velocityEngine = new VelocityEngine(template, params);
		String command = velocityEngine.mergeTemplate();

		log.debug(command);
		return command;

	}

	public void responseReceived(ICapabilityMessage responseMessage)
			throws CommandException {
		if (responseMessage instanceof ProtocolResponseMessage) {

			log.info("Response is OK");
			this.response = (ProtocolResponseMessage) responseMessage;
			this.requestEngineModel(false);

		} else if (responseMessage instanceof ProtocolErrorMessage) {
			log.info("It ocurred an error");

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

}
