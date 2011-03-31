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

	private String					protocolId;
	private BundleContext			bundleContext;
	private IProtocolSessionManager	protocolSessionManager;
	private CapabilityDescriptor	descriptor;

	public Action newActionWithContext(BundleContext bundleContext,
			IProtocolSessionManager protocolSessionManager,
			CapabilityDescriptor descriptor) {
		Action action = new Action();
		action.bundleContext = bundleContext;
		action.protocolSessionManager = protocolSessionManager;
		action.descriptor = descriptor;
		return action;
	}

	public ActionResponse execute(Object params) throws ProtocolException,
			CommandException {

		ActionResponse actionResponse = new ActionResponse();

		log.info("executing commands for an action");

		ProtocolSessionContext protocolSessionContext = getSessionContext();

		ProtocolNetconfWrapper protocolWrapper = ProtocolNetconfWrapper
				.newActionWrapper(bundleContext, protocolSessionManager);

		IProtocolSession protocol = protocolWrapper
				.getProtocolSession(protocolSessionContext);

		for (Command command : commands) {

			// protocolSessionManager.getProtocolSession(arg0, arg1);

			log.info("initializing");
			command.setParams(params);
			command.initialize();
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

		protocolWrapper.releaseProtocolSession();

		return actionResponse;

	}

	private ProtocolSessionContext getSessionContext() {
		String protocolURI = descriptor
				.getPropertyValue(ResourceDescriptorConstants.PROTOCOL);

		// merge user and password with protocol
		// TODO IT IS HARDCODED THE TRANSPORT!!! IF WILL USE OTHER TRANSPORT IT
		// IS NECESSARY TO CHANGE THIS.
		protocolURI = "ssh://" + protocolURI + "/" + protocolId;

		ProtocolSessionContext sessionContext = new ProtocolSessionContext();
		sessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI,
				protocolURI);

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

	public String getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
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

}
