package org.opennaas.extensions.roadm.capability.connections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;

public class ConnectionsCapability extends AbstractCapability implements IConnectionsCapability {

	public static final String	CAPABILITY_TYPE	= "connections";

	public static String		CONNECTIONS		= CAPABILITY_TYPE;

	Log							log				= LogFactory.getLog(ConnectionsCapability.class);

	private String				resourceId		= "";

	public ConnectionsCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Connections Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IConnectionsCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityName()
	 */
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#getActionSet()
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getConnectionsActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	// IConnectionsCapability implementation

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.roadm.capability.connections.IConnectionsCapability#makeConnection(org.opennaas.extensions.router.model.opticalSwitch
	 * .FiberConnection)
	 */
	@Override
	public void makeConnection(FiberConnection connectionRequest)
			throws CapabilityException {

		log.info("Start of makeConnection call");
		IAction action = createActionAndCheckParams(ConnectionsActionSet.MAKE_CONNECTION, connectionRequest);
		queueAction(action);
		log.info("End of makeConnection call");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.roadm.capability.connections.IConnectionsCapability#removeConnection(org.opennaas.extensions.router.model.opticalSwitch
	 * .FiberConnection)
	 */
	@Override
	public void removeConnection(FiberConnection connectionRequest)
			throws CapabilityException {
		log.info("Start of removeConnection call");
		IAction action = createActionAndCheckParams(ConnectionsActionSet.REMOVE_CONNECTION, connectionRequest);
		queueAction(action);
		log.info("End of removeConnection call");

	}

}
