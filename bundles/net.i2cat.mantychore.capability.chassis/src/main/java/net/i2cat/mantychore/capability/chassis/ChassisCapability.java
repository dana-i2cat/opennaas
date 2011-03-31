package net.i2cat.mantychore.capability.chassis;

import java.util.List;
import java.util.Vector;

import net.i2cat.mantychore.actionsets.junos.ChassisActionSetFactory;
import net.i2cat.mantychore.commons.AbstractMantychoreCapability;
import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionException;
import net.i2cat.mantychore.commons.ErrorConstants;
import net.i2cat.mantychore.commons.IActionSetFactory;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.CapabilityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChassisCapability extends AbstractMantychoreCapability {

	public final static String			CHASSIS				= "chassis";

	Logger								log					= LoggerFactory
																	.getLogger(ChassisCapability.class);

	private final QueueManagerWrapper	queueManagerWrapper	= new QueueManagerWrapper();
	// private final List<String> basicActionIds = new ArrayList<String>();

	private IQueueManagerService		queueManager;

	public ChassisCapability(List<String> actionIds, IResource resource) {
		super(actionIds, resource);
	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		queueManager = queueManagerWrapper.getQueueManager(resource
				.getResourceDescriptor().getId());

	}

	@Override
	public Response sendMessage(String idOperation, Object params) {
		// Check if it is an available operation
		if (!actionIds.contains(idOperation)) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs.add(ErrorConstants.ERROR_CAPABILITY);
			Response.errorResponse(idOperation, errorMsgs);
		}

		// FIXME IT HAS TO BE CALLED THROUGH OSGI SERVICE
		// IS IT NECESSARY TO SPECIFY WITH CHASSIS CAPABILITY??
		IActionSetFactory actionFactory = new ChassisActionSetFactory();
		Action action = null;
		try {
			action = actionFactory.createAction(idOperation);
		} catch (ActionException e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			Response.errorResponse(idOperation, errorMsgs);
		}

		// FIXME how we can add the model in the action
		action.setContextParams(resource.getResourceDescriptor()
				.getProperties());

		action.setModelToUpdate(resource.getModel());

		queueManager.queueAction(action, params);
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

}
