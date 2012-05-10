package org.opennaas.extensions.bod.capability.l2bod;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

public class L2BoDCapability extends AbstractCapability implements IL2BoDCapability {

	public static String	CAPABILITY_TYPE	= "l2bod";

	Log						log				= LogFactory.getLog(L2BoDCapability.class);

	private String			resourceId		= "";

	public L2BoDCapability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new L2BoD Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability#requestConnection(org.opennaas.extensions.bod.capability.l2bod.
	 * RequestConnectionParameters)
	 */
	@Override
	public void requestConnection(RequestConnectionParameters parameters) throws CapabilityException {
		IAction action = createActionAndCheckParams(L2BoDActionSet.REQUEST_CONNECTION, parameters);
		queueAction(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability#shutDownConnection(java.util.List)
	 */
	@Override
	public void shutDownConnection(List<Interface> listInterfaces) throws CapabilityException {
		IAction action = createActionAndCheckParams(L2BoDActionSet.SHUTDOWN_CONNECTION, listInterfaces);
		queueAction(action);
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {

		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getL2BoDActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
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
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

}
