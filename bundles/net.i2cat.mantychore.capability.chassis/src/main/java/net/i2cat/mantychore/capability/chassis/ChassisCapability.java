package net.i2cat.mantychore.capability.chassis;

import java.util.Vector;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChassisCapability extends AbstractCapability {

	public final static String	CHASSIS		= "chassis";

	Log							log			= LogFactory.getLog(ChassisCapability.class);

	private String				resourceId	= "";

	public ChassisCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Chassis Capability");
	}

	@Override
	protected void initializeCapability() throws CapabilityException {

	}

	@Override
	public Object sendMessage(String idOperation, Object params) {
		log.debug("Sending message to Chassis Capability");
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

		return Response.okResponse(idOperation);
	}

	@Override
	protected void activateCapability() throws CapabilityException {

	}

	@Override
	protected void deactivateCapability() throws CapabilityException {

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {

	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String protocol = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_PROTOCOL);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getChassisActionSetService(name, version, protocol);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

}
