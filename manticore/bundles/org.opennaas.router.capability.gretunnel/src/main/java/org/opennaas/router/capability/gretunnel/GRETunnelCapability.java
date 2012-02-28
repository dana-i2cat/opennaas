package org.opennaas.router.capability.gretunnel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.GRETunnelService;
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
 * @author Jordi
 * 
 */
public class GRETunnelCapability extends AbstractCapability implements IGRETunnelService {

	public final static String	CAPABILITY_NAME	= "gretunnel";

	private String				resourceID		= "";
	Log							log				= LogFactory.getLog(GRETunnelCapability.class);

	/**
	 * @param descriptor
	 * @param resourceID
	 */
	public GRETunnelCapability(CapabilityDescriptor descriptor, String resourceID) {
		super(descriptor);
		this.resourceID = resourceID;
		log.debug("Built new GRE Tunnel Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#sendMessage(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object sendMessage(String idOperation, Object params) throws CapabilityException {
		log.debug("Sending message to GRE Tunnel Capability");
		try {
			IQueueManagerService queueManager = Activator.getQueueManagerService(resourceID);
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
			return Activator.getGRETunnelActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.gretunnel.IGRETunnelService#createGRETunnel(net.i2cat.mantychore.model.GRETunnelService)
	 */
	@Override
	public Response createGRETunnel(GRETunnelService greTunnelService) throws CapabilityException {
		return (Response) sendMessage(ActionConstants.CREATETUNNEL, greTunnelService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.gretunnel.IGRETunnelService#deleteGRETunnel(net.i2cat.mantychore.model.GRETunnelService)
	 */
	@Override
	public Response deleteGRETunnel(GRETunnelService greTunnelService) throws CapabilityException {
		return (Response) sendMessage(ActionConstants.DELETETUNNEL, greTunnelService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.router.capability.gretunnel.IGRETunnelService#showGRETunnelConfiguration()
	 */
	@Override
	public List<GRETunnelService> showGRETunnelConfiguration() throws CapabilityException {
		List<GRETunnelService> listGreTunnelServices = new ArrayList<GRETunnelService>();
		List<Service> lServices = ((ComputerSystem) resource.getModel()).getHostedService();

		// If hosted services is null or empty throw Exception
		if (lServices == null || lServices.size() <= 0) {
			throw new CapabilityException("No hosted services in this model.");
		} else {
			// Search OSPF Service in the Service list
			for (Service service : lServices) {
				if (service instanceof GRETunnelService) {
					listGreTunnelServices.add((GRETunnelService) service);
				}
			}
		}

		return listGreTunnelServices;
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
	 * @see org.opennaas.core.resources.capability.AbstractCapability#shutdownCapability()
	 */
	@Override
	protected void shutdownCapability() throws CapabilityException {
		// Nothing to do
	}

}
