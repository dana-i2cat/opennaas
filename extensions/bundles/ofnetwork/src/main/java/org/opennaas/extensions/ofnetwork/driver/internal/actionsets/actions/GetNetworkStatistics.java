package org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions;

import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.ofnetwork.Activator;
import org.opennaas.extensions.ofnetwork.model.NetworkStatistics;
import org.opennaas.extensions.openflowswitch.capability.monitoring.IMonitoringCapability;
import org.opennaas.extensions.openflowswitch.capability.monitoring.SwitchPortStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class GetNetworkStatistics extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		ActionResponse response = new ActionResponse();

		try {
			List<IResource> resources = getNetworkSwitches();
			NetworkStatistics netStats = getSwitchesStatistics(resources);
			response.setResult(netStats);
			response.setStatus(ActionResponse.STATUS.OK);

		} catch (ActivatorException ae) {
			throw new ActionException(ae);
		} catch (ResourceException e) {
			throw new ActionException(e);
		}

		return response;
	}

	private NetworkStatistics getSwitchesStatistics(List<IResource> resources) throws ResourceException {
		NetworkStatistics netStats = new NetworkStatistics();
		for (IResource resource : resources) {
			IMonitoringCapability monitorCapab = (IMonitoringCapability) resource.getCapabilityByInterface(IMonitoringCapability.class);
			SwitchPortStatistics switchStatistics = monitorCapab.getPortStatistics();
			netStats.addPortSwitchStatistic(resource.getResourceDescriptor().getInformation().getName(), switchStatistics);
		}

		return netStats;

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params != null)
			log.debug("Params ignored for action " + this.getActionID());

		return true;
	}

	/**
	 * TODO to be removed when network is aware of its topology
	 * 
	 * @return
	 * @throws ActivatorException
	 */
	private List<IResource> getNetworkSwitches() throws ActivatorException {

		IResourceManager rm = Activator.getResourceManagerService();
		List<IResource> ofSwitches = rm.listResourcesByType("openflowswitch");

		return ofSwitches;
	}

}
