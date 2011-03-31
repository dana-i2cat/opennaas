package net.i2cat.mantychore.capability.chassis;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.i2cat.mantychore.commons.AbstractMantychoreCapability;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ErrorConstants;
import net.i2cat.mantychore.commons.IActionSetFactory;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisCapability extends AbstractMantychoreCapability {

	public final static String		CHASSIS	= "chassis";

	Logger							log		= LoggerFactory
													.getLogger(ChassisCapability.class);

	// private final List<String> basicActionIds = new ArrayList<String>();

	// TODO HOW WE CAN PASS THE QUEUEMANAGER
	private IQueueManagerService	queueManager;

	public ChassisCapability(List<String> actionIds, IResource resource) {
		super(actionIds, resource, resource.getResourceDescriptor()
				.getCapabilityDescriptor(CHASSIS));
	}

	@Override
	protected void initializeCapability() throws CapabilityException {

	}

	@Override
	public Response sendMessage(String idOperation, Object params) {
		// Check if it is an available operation
		if (!actionIds.contains(idOperation)) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs.add(ErrorConstants.ERROR_CAPABILITY);
			return Response.errorResponse(idOperation, errorMsgs);
		}

		Action action = null;
		try {
			IActionSetFactory actionFactory = (IActionSetFactory) getCapability(
					IActionSetFactory.class, Activator.getContext(),
					createFilterProperties(descriptor));
			action = actionFactory.createAction(idOperation);

			action.setParams(params);
			action.setDescriptor(descriptor);
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

	/*
	 * necessary to get some capability type
	 */

	protected Properties createFilterProperties(
			CapabilityDescriptor capabilityDescriptor) {
		Properties properties = new Properties();
		properties
				.put(ResourceDescriptorConstants.ACTION_NAME,
						capabilityDescriptor
								.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME));

		properties
				.put(ResourceDescriptorConstants.ACTION_VERSION,
						capabilityDescriptor
								.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION));

		properties
				.put(ResourceDescriptorConstants.ACTION_PROTOCOL,
						capabilityDescriptor
								.getPropertyValue(ResourceDescriptorConstants.ACTION_PROTOCOL));

		return properties;
	}

	public IQueueManagerService getQueueManager() {
		return queueManager;
	}

	public void setQueueManager(IQueueManagerService queueManager) {
		this.queueManager = queueManager;
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

}
