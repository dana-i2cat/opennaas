package org.opennaas.extensions.router.capability.ip;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerService;

public class IPCapability extends AbstractCapability {
	public final static String	IPv4		= "ipv4";

	Log							log			= LogFactory.getLog(IPCapability.class);

	private String				resourceId	= "";

	public IPCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new IP Capability");
	}

	@Override
	protected void activateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getIPActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}

	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object sendMessage(String idOperation, Object params) throws CapabilityException {
		log.debug("Sending message to IP Capability");
		try {
			IQueueManagerService queueManager = Activator.getQueueManagerService(resourceId);
			IAction action = createAction(idOperation);

			action.setParams(params);
			action.setModelToUpdate(resource.getModel());
			queueManager.queueAction(action);

		} catch (Exception e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			return Response.errorResponse(idOperation, errorMsgs);
		}

		return Response.queuedResponse(idOperation);
	}

	private Response prepareErrorMessage(String nameError, String message) {
		Vector<String> errorMsgs = new Vector<String>();
		errorMsgs.add(message);
		return Response.errorResponse(nameError, errorMsgs);

	}
}
