package org.opennaas.extensions.router.capability.gretunnel;

import java.util.ArrayList;
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
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.Service;

/**
 * @author Jordi
 * 
 */
public class GRETunnelCapability extends AbstractCapability implements IGRETunnelCapability {

	public final static String	CAPABILITY_TYPE	= "gretunnel";

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
	 * @see
	 * org.opennaas.extensions.router.capability.gretunnel.IGRETunnelService#createGRETunnel(org.opennaas.extensions.router.model.GRETunnelService)
	 */
	@Override
	public void createGRETunnel(GRETunnelService greTunnelService) throws CapabilityException {
		IAction action = createActionAndCheckParams(GRETunnelActionSet.CREATETUNNEL, greTunnelService);
		queueAction(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.router.capability.gretunnel.IGRETunnelService#deleteGRETunnel(org.opennaas.extensions.router.model.GRETunnelService)
	 */
	@Override
	public void deleteGRETunnel(GRETunnelService greTunnelService) throws CapabilityException {
		IAction action = createActionAndCheckParams(GRETunnelActionSet.DELETETUNNEL, greTunnelService);
		queueAction(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.router.capability.gretunnel.IGRETunnelService#showGRETunnelConfiguration()
	 */
	@Override
	public List<GRETunnelService> showGRETunnelConfiguration() throws CapabilityException {
		List<GRETunnelService> listGreTunnelServices = new ArrayList<GRETunnelService>();
		List<Service> lServices = ((ComputerSystem) resource.getModel()).getHostedService();

		// If hosted services is null or empty throw Exception
		if (lServices != null) {
			// Search GRETunnel Service in the Service list
			for (Service service : lServices) {
				if (service instanceof GRETunnelService) {
					listGreTunnelServices.add((GRETunnelService) service);
				}
			}
		}

		return listGreTunnelServices;
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceID).queueAction(action);
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
