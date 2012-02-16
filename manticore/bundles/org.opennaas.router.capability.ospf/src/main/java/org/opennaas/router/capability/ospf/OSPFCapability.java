package org.opennaas.router.capability.ospf;

import java.util.Vector;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.OSPFService;
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
	 * @see org.opennaas.router.capability.ospf.IOSPFService#configureOSPF()
	 */
	@Override
	public Object configureOSPF(Object params) {

		return sendMessage(ActionConstants.OSPF_CONFIGURE, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#activateOSPF(java.lang.String, java.lang.String)
	 */
	@Override
	public Object activateOSPF(Object params) {
		// TODO Auto-generated method stub
		return sendMessage(ActionConstants.OSPF_ACTIVATE, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#deactivateOSPF(java.lang.String, java.lang.String)
	 */
	@Override
	public Object deactivateOSPF(Object params) {
		// TODO Auto-generated method stub
		return sendMessage(ActionConstants.OSPF_DEACTIVATE, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#showOSPFConfiguration()
	 */
	@Override
	public OSPFService showOSPFConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.ospf.IOSPFService#getOSPFConfiguration(net.i2cat.mantychore.model.OSPFService)
	 */
	@Override
	public Object getOSPFConfiguration(OSPFService ospfService) {

		return sendMessage(ActionConstants.OSPF_GET_CONFIGURATION, ospfService.getRouterID());
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
