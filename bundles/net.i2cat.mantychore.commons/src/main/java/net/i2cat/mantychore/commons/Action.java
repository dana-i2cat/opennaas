package net.i2cat.mantychore.commons;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.commons.Response.Status;
import net.i2cat.mantychore.commons.wrappers.ProtocolNetconfWrapper;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Action {

	Logger							log			= LoggerFactory
														.getLogger(Action.class);

	protected List<Command>			commands	= new ArrayList<Command>();
	private Object					modelToUpdate;

	private CapabilityDescriptor	descriptor;
	private Object					params;

	public ActionResponse execute(BundleContext bundleContext,
			IProtocolSessionManager protocolSessionManager)
			throws ActionException {

		ActionResponse actionResponse = new ActionResponse();

		log.info("executing commands for an action");

		ProtocolSessionContext protocolSessionContext = getSessionContext();

		ProtocolNetconfWrapper protocolWrapper = ProtocolNetconfWrapper
				.newActionWrapper(bundleContext, protocolSessionManager);

		try {
			IProtocolSession protocol = protocolWrapper
					.getProtocolSession(protocolSessionContext);

			for (Command command : commands) {

				// protocolSessionManager.getProtocolSession(arg0, arg1);

				log.info("initializing");
				command.setParams(params);
				command.initialize();

				log.info("sending...");

				Response response = sendCommandToProtocol(command, protocol);

				actionResponse.addResponse(response);

				if (response.getStatus().equals(Status.ERROR)) {
					// exit from the bucle, it is necessary
					break;
				}

			}

			protocolWrapper.releaseProtocolSession();

		} catch (ProtocolException e) {
			ActionException actionException = new ActionException(
					e.getMessage());
			actionException.initCause(e);
			throw actionException;

		} catch (CommandException e) {
			ActionException actionException = new ActionException(
					e.getMessage());
			actionException.initCause(e);
			throw actionException;
		}

		return actionResponse;

	}

	private ProtocolSessionContext getSessionContext() {
		String protocolURI = descriptor
				.getPropertyValue(ResourceDescriptorConstants.PROTOCOL_URI);
		String protocolId = descriptor
				.getPropertyValue(ResourceDescriptorConstants.ACTION_PROTOCOL);

		// merge user and password with protocol
		// TODO IT IS HARDCODED THE TRANSPORT!!! IF WILL USE OTHER TRANSPORT IT
		// IS NECESSARY TO CHANGE THIS.
		if (!protocolURI.startsWith("mock://"))
			protocolURI = "ssh://" + protocolURI + "/" + protocolId;

		ProtocolSessionContext sessionContext = new ProtocolSessionContext();
		sessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI,
				protocolURI);

		/* the protocolsessioncontext is the same that the action_protocol */
		sessionContext
				.addParameter(ProtocolSessionContext.PROTOCOL, protocolId);

		return sessionContext;
	}

	private Response sendCommandToProtocol(Command command,
			IProtocolSession protocol) throws ProtocolException,
			CommandException {
		log.info("sending and parsing message");
		Object messageResp = protocol.sendReceive(command.message());
		Response response = command.checkResponse(messageResp);
		// If it was not problems with the operation, we can update the model
		if (response.getStatus().equals(Response.Status.OK)) {
			command.parseResponse(messageResp, modelToUpdate);
		}
		return response;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	/* Added parameters from the resource */

	public Object getModelToUpdate() {
		return modelToUpdate;
	}

	public void setModelToUpdate(Object modelToUpdate) {
		this.modelToUpdate = modelToUpdate;
	}

	public CapabilityDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

}
