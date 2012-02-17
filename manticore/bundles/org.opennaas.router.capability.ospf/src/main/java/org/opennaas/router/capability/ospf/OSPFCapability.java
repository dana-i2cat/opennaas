package org.opennaas.router.capability.ospf;

import java.util.List;
import java.util.Vector;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.OSPFService;
import net.i2cat.mantychore.model.Service;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;

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

/**
 * @author Jordi Puig
 */
public class OSPFCapability extends AbstractCapability implements IOSPFService {

	public static String	CAPABILITY_NAME	= "ospf";

	Log						log				= LogFactory.getLog(OSPFCapability.class);

	private String			resourceId		= "";

	/**
	 * OSPFCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public OSPFCapability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new OSPF Capability");
	}

	/**
	 * Execute the action defined in the idOperation param
	 * 
	 * @param idOperation
	 * @param params
	 */
	@Override
	public Object sendMessage(String idOperation, Object params) {

		log.debug("Sending message to OSPF Capability");
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

	/**
	 * Return the OSPF ActioSet
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {

		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getOSPFActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#activateOSPF(java.util.List)
	 */
	@Override
	public Object activateOSPF(List<LogicalPort> lLogicalPort) {
		Response response = null;

		for (LogicalPort logicalPort : lLogicalPort) {
			response = (Response) sendMessage(ActionConstants.OSPF_ACTIVATE, logicalPort);
			if (response.getStatus().equals(Response.Status.OK)) {
				break;
			}
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#deactivateOSPF(java.util.List)
	 */
	@Override
	public Object deactivateOSPF(List<LogicalPort> lLogicalPort) {

		return sendMessage(ActionConstants.OSPF_DEACTIVATE, lLogicalPort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#configureOSPF(net.i2cat.mantychore.model.OSPFService)
	 */
	@Override
	public Object configureOSPF(OSPFService ospfService) {

		return sendMessage(ActionConstants.OSPF_CONFIGURE, ospfService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#getOSPFConfiguration()
	 */
	@Override
	public OSPFService getOSPFConfiguration() {

		return (OSPFService) sendMessage(ActionConstants.OSPF_GET_CONFIGURATION, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#showOSPFConfiguration()
	 */
	@Override
	public OSPFService showOSPFConfiguration() throws CapabilityException {
		OSPFService ospfService = null;

		List<Service> lServices = ((ComputerSystem) resource.getModel()).getHostedService();
		// If hosted services is null or empty throw Exception
		if (lServices == null || lServices.size() <= 0) {
			throw new CapabilityException("No hosted services in this model.");
		} else {
			// Search OSPF Service in the Service list
			for (Service service : lServices) {
				if (service instanceof OSPFService) {
					ospfService = (OSPFService) service;
					break;
				}
			}
		}

		return ospfService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activateCapability()
	 */
	@Override
	protected void activateCapability() throws CapabilityException {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivateCapability()
	 */
	@Override
	protected void deactivateCapability() throws CapabilityException {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#initializeCapability()
	 */
	@Override
	protected void initializeCapability() throws CapabilityException {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#shutdownCapability()
	 */
	@Override
	protected void shutdownCapability() throws CapabilityException {
		// Nothing to do
	}

}
