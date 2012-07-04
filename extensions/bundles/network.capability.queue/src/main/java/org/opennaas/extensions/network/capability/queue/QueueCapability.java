package org.opennaas.extensions.network.capability.queue;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

/**
 * @author Jordi Puig
 */
public class QueueCapability extends AbstractCapability implements IQueueCapability {

	public final static String	CAPABILITY_TYPE				= "netqueue";

	public final static String	NETQUEUE_CAPABILITY_NAME	= CAPABILITY_TYPE;

	public final static String	QUEUE_CAPABILITY_NAME		= "queue";

	Log							log							= LogFactory.getLog(QueueCapability.class);

	private String				resourceId					= "";

	/**
	 * QueueCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public QueueCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new queue capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		// registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceName(), IQueueCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		// registration.unregister();
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#getActionSet()
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.network.capability.queue.IQueueService#execute()
	 */
	@Override
	public Map<String, QueueResponse> execute() throws CapabilityException {
		log.info("Start of execute call");
		Map<String, QueueResponse> response = new Hashtable<String, QueueResponse>();
		NetworkModel model = (NetworkModel) resource.getModel();
		if (model.getNetworkElements() != null && !model.getNetworkElements().isEmpty()) {
			for (NetworkElement networkElement : model.getNetworkElements()) {
				// Only Device and NetworkDomain instances
				if (networkElement instanceof Device || networkElement instanceof NetworkDomain) {
					try {
						QueueResponse queueResponse = executeQueue(networkElement.getName());
						if (queueResponse != null) {
							response.put(networkElement.getName(), queueResponse);
						}
					} catch (CapabilityException e) {
						response.put(networkElement.getName(), new QueueResponse());
					}
				}
			}
		}
		log.info("End of execute call");
		return response;
	}

	/**
	 * Execute the queue of the NetworkElement name
	 * 
	 * @param networkElementName
	 * 
	 * @return queue response
	 * @throws CapabilityException
	 */
	private QueueResponse executeQueue(String networkElementName) throws CapabilityException {
		QueueResponse queueResponse = null;
		try {
			IResource iResource = getResource(networkElementName);
			if (iResource != null) {

				IQueueManagerCapability queueCapability = (IQueueManagerCapability) iResource
						.getCapabilityByInterface(IQueueManagerCapability.class);
				if (queueCapability != null) {
					queueResponse = queueCapability.execute();
				}
			}
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		} catch (ProtocolException e) {
			throw new CapabilityException(e);
		}
		return queueResponse;
	}

	/**
	 * Get the resource from resourceManager.<br/>
	 * To get the resource the name must have the pattern resourceType:resourceName
	 * 
	 * @param networkElementName
	 *            the notworkElement name with pattern resourceType:resourceName
	 * @return IResource
	 * @throws ResourceException
	 * @throws ActivatorException
	 */
	private IResource getResource(String networkElementName) throws ResourceException, ActivatorException {
		IResource iResource = null;
		String[] aResourceName = getResourceTypeAndName(networkElementName);
		if (aResourceName.length > 1) {
			IResourceManager resourceManager = Activator.getResourceManagerService();
			IResourceIdentifier iResourceIdentifier = resourceManager
					.getIdentifierFromResourceName(aResourceName[0], aResourceName[1]);
			iResource = resourceManager.getResource(iResourceIdentifier);
		}
		return iResource;
	}

	/**
	 * Get the resource type and the resource name in a string array from pattern resourceType:resourceName
	 * 
	 * @param name
	 *            with pattern resourceType:resourceName
	 * @return string array
	 */
	private String[] getResourceTypeAndName(String name) {
		return name.split(":");
	}
}
