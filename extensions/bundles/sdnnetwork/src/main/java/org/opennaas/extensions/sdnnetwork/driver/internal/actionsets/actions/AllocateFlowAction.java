package org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.actions;

import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.sdnnetwork.Activator;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.Port;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkModel;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class AllocateFlowAction extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		SDNNetworkOFFlow flow = (SDNNetworkOFFlow) params;
		// provision each link and mark the last one
		List<NetworkConnection> connections = flow.getRoute().getNetworkConnections();
		for (int i = 0; i < connections.size(); i++) {
			NetworkConnection networkConnection = connections.get(i);
			provisionLink(networkConnection.getSource(), networkConnection.getDestination(), new SDNNetworkOFFlow(flow), i == connections.size() - 1);
		}
		return ActionResponse.okResponse(getActionID());
	}

	private void provisionLink(Port source, Port destination, SDNNetworkOFFlow sdnNetworkOFFlow, boolean lastLink) throws ActionException {
		if (source.getDeviceId() != destination.getDeviceId()) {
			/* link between different devices, assume it exists or it is provisioned */
		} else {
			/* link inside same device, use device internal capability to provision it */
			// get OpenNaaS resource Id from the map in the model
			String deviceId = source.getDeviceId();
			String resourceId = ((SDNNetworkModel) getModelToUpdate()).getDeviceResourceMap().get(deviceId);
			if (resourceId == null) {
				throw new ActionException("No resource Id found from device Id: " + deviceId);
			}

			// get switch resource from Resource Manager using resource Id
			IResource resource = null;
			try {
				resource = Activator.getResourceManagerService().getResourceById(resourceId);
			} catch (Exception e) {
				throw new ActionException("Error getting switch resource from Resource Manager", e);
			}

			// get IOpenflowForwardingCapability from the obtained resource
			IOpenflowForwardingCapability capability = null;
			try {
				capability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
			} catch (ResourceException e) {
				throw new ActionException("Error getting IOpenflowForwardingCapability from resource with Id: " + resourceId, e);
			}

			// construct FloodlightOFFlow based on SDNNetworkOFFlow, source Port, destination Port and lastLink
			FloodlightOFFlow flow = new FloodlightOFFlow(sdnNetworkOFFlow, deviceId);

			flow.getMatch().setIngressPort(source.getPortNumber());

			// Only last link in the flow should apply actions other than forwarding.
			// The rest of the links should have only forwarding actions.
			List<FloodlightOFAction> actions = flow.getActions();
			if (!lastLink) {
				FloodlightOFAction outputAction = null;
				for (FloodlightOFAction floodlightOFAction : actions) {
					if (floodlightOFAction.getType() == FloodlightOFAction.TYPE_OUTPUT) {
						outputAction = floodlightOFAction;
					}
				}
				if (outputAction == null) {
					throw new ActionException("No output action found in FloodlightOFFlow.");
				}
				// clear list and add output action
				actions.clear();
				actions.add(outputAction);
			}

			// invoke IOpenflowForwardingCapability
			try {
				capability.createOpenflowForwardingRule(flow);
			} catch (CapabilityException e) {
				throw new ActionException("Error executing IOpenflowForwardingCapability from resource with Id: " + resourceId, e);
			}
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params instanceof SDNNetworkOFFlow) {
			SDNNetworkOFFlow flow = (SDNNetworkOFFlow) params;
			Route route = flow.getRoute();
			if (route == null) {
				throw new ActionException("Route must be set");
			}
			List<NetworkConnection> connections = route.getNetworkConnections();
			if (connections == null || connections.size() == 0) {
				throw new ActionException("NetworkConnection list must have at least 1 item");
			}

			return true;
		}
		throw new ActionException("Action parameters must be set and SDNNetworkOFFlow type");
	}
}
