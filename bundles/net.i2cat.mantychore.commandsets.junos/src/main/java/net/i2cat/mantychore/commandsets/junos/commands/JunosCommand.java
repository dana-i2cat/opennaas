package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.mantychore.commandsets.junos.velocity.VelocityEngine;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.netconf.rpc.Query;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JunosCommand extends Command {

	protected String	template	= "";

	private Object		params;

	/** logger **/
	Logger				log			= LoggerFactory
																.getLogger(JunosCommand.class);
	protected Query		command;
	protected String	netconfXML;

	// protected Reply response;

	protected JunosCommand(String commandID, String template) {
		this.setCommandId(commandID);
		this.template = template;
	}

	// // FIXME IT IS TEMPORAL, THESE SET AND GETS MUST BE DELETED
	// public Object getParams() {
	// return params;
	// }
	//
	// public void setParams(Object params) {
	// this.params = params;
	// }
	//
	// @Override
	// public void initializeCommand(IResourceModel arg0) throws CommandException {
	// // FIXME DO WE WANT TO INITIALIZE OUR COMMAND WITH PARAMETERS FROM
	// // IRESOURCEMODEL??
	// }
	//
	// @Override
	// public void executeCommand() throws CommandException {
	// try {
	// netconfXML = prepareVelocityCommand();
	// createCommand();
	// // TODO THIS METHOD WILL RECEIVE AN OBJECT
	// // the commands no implement this method yet (depends on queue)
	// // sendCommandToProtocol(command);
	// } catch (ResourceNotFoundException e) {
	// log.error(e.getMessage());
	// } catch (ParseErrorException e) {
	// log.error(e.getMessage());
	// } catch (Exception e) {
	// log.error(e.getMessage());
	// }
	//
	// }
	//
	// public void sendCommandToProtocol(Object command) {
	// // TODO Auto-generated method stub
	// }
	//
	// public abstract void createCommand();

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		try {
			netconfXML = prepareVelocityCommand();
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

	protected String prepareVelocityCommand() throws ResourceNotFoundException,
			ParseErrorException, Exception {
		VelocityEngine velocityEngine = new VelocityEngine(template, params);
		String command = velocityEngine.mergeTemplate();
		log.debug("Command from velocity (netconfXML)" + command);
		return command;
	}

	// public void responseReceived(ICapabilityMessage responseMessage)
	// throws CommandException {
	// if (responseMessage instanceof ProtocolResponseMessage) {
	//
	// log.info("Response is OK");
	// this.response = (ProtocolResponseMessage) responseMessage;
	// this.requestEngineModel(false);
	//
	// } else if (responseMessage instanceof ProtocolErrorMessage) {
	// log.info("It ocurred an error");
	//
	// this.errorMessage = (ProtocolErrorMessage) responseMessage;
	// CommandException commandException = new CommandException(
	// "Error executing command " + this.commandID,
	// ((ProtocolErrorMessage) responseMessage)
	// .getProtocolException());
	//
	// commandException.setName(this.commandID);
	// commandException.setCommand(this);
	//
	// this.sendCommandErrorResponse(commandException);
	//
	// }
	// }

}
