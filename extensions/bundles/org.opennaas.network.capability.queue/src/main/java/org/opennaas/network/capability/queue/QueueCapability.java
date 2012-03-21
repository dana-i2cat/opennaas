package org.opennaas.network.capability.queue;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.domain.NetworkDomain;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.NetworkElement;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;

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
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;

/**
 * @author Jordi Puig
 */
public class QueueCapability extends AbstractCapability implements IQueueService {

	public final static String	NETQUEUE_CAPABILITY_NAME	= "netqueue";

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
	 * Execute the action defined in the idOperation param (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#sendMessage(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object sendMessage(String idOperation, Object params) {
		log.debug("Sending message to queue capability");
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
	 * @see org.opennaas.network.capability.queue.IQueueService#execute()
	 */
	@Override
	public Map<String, QueueResponse> execute() throws CapabilityException {
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
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activateCapability()
	 */
	@Override
	protected void activateCapability() throws CapabilityException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivateCapability()
	 */
	@Override
	protected void deactivateCapability() throws CapabilityException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#initializeCapability()
	 */
	@Override
	protected void initializeCapability() throws CapabilityException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#shutdownCapability()
	 */
	@Override
	protected void shutdownCapability() throws CapabilityException {
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
				ICapability queueCapability = iResource.getCapability(getInformation(QUEUE_CAPABILITY_NAME));
				if (queueCapability != null) {
					queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
				}
			}
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		} catch (ActivatorException e) {
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
	 * Get the information class with the type = _type
	 * 
	 * @param type
	 *            to set
	 * @return information
	 */
	private Information getInformation(String _type) {
		Information information = new Information();
		information.setType(_type);
		return information;
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
