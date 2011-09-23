package net.i2cat.mantychore.capability.chassis;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.ManagedSystemElement;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
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

			// Add logical router access
			if (params != null &&
					isALogicalRouter()) {
				addParamsForLogicalRouters(params);
			}

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

	private void addParamsForLogicalRouters(Object params) {
		if (params == null)
			return;
		((ManagedSystemElement) params).setElementName(resource.getResourceDescriptor().getInformation().getName());
	}

	public boolean isALogicalRouter() {
		ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
		/* Check that the logical router exists */
		if (resourceDescriptor == null || resourceDescriptor.getProperties() == null)
			return false;

		return (resourceDescriptor.getProperties().get(ResourceDescriptor.VIRTUAL) != null && resourceDescriptor
				.getProperties()
				.get(ResourceDescriptor.VIRTUAL).equals("true"));
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

	/**
	 * Override to use refreshActions for chassis capability in Junos
	 * */
	public Response sendRefreshActions() {
		List<String> refreshActions;
		try {
			refreshActions = this.getActionSet().getRefreshActionName();
		} catch (CapabilityException e) {
			return prepareErrorMessage("STARTUP_REFRESH_ACTION", e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
		}

		List params = new ArrayList();
		boolean isLogical = isALogicalRouter();

		for (int index = 0; index < refreshActions.size(); index++) {
			if (isLogical) {
				ComputerSystem param = new ComputerSystem();
				addParamsForLogicalRouters(param);
				params.add(param);

			}
		}

		return super.sendRefreshActions(params);

	}

	private Response prepareErrorMessage(String nameError, String message) {
		Vector<String> errorMsgs = new Vector<String>();
		errorMsgs.add(message);
		return Response.errorResponse(nameError, errorMsgs);

	}

}
