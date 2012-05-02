package org.opennaas.extensions.router.capability.staticroute;

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
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;

/**
 * @author Jordi Puig
 */
public class StaticRouteCapability extends AbstractCapability implements IStaticRouteService {

	public static String	CAPABILITY_NAME	= "staticroute";

	Log						log				= LogFactory.getLog(StaticRouteCapability.class);

	private String			resourceId		= "";

	/**
	 * StaticRouteCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public StaticRouteCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new StaticRoute Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_NAME;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to.
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerService getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

	/**
	 * Execute the action defined in the idOperation param
	 * 
	 * @param idOperation
	 * @param params
	 */
	private Object sendMessage(String idOperation, Object params) {
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

	/**
	 * Return the Static Route ActionSet
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);
		try {
			return Activator.getStaticRouteActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.staticroute.IStaticRouteService#create(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Response create(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException {
		String[] aParams = new String[3];
		aParams[0] = netIdIpAdress;
		aParams[1] = maskIpAdress;
		aParams[2] = nextHopIpAddress;
		return (Response) sendMessage(ActionConstants.STATIC_ROUTE_CREATE, aParams);
	}
}
