package org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.actions;

import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.sdnnetwork.Activator;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.Port;
import org.opennaas.extensions.sdnnetwork.model.Route;
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
			try {
				provisionLink(networkConnection.getSource(), networkConnection.getDestination(), new SDNNetworkOFFlow(flow),
						i == connections.size() - 1);
			} catch (Exception e) {
				throw new ActionException("Error provisioning link : ", e);
			}
		}
		return ActionResponse.okResponse(getActionID());
	}

	private void provisionLink(Port source, Port destination, SDNNetworkOFFlow sdnNetworkOFFlow, boolean lastLink) throws ResourceException,
			ActivatorException {
		if (source.getDeviceId() != destination.getDeviceId()) {
			/* link between different devices, assume it exists or it is provisioned */
		} else {
			/* link inside same device, use device internal capability to provision it */
			String resourceName = source.getDeviceId();
			IResource resource = getResourceByName(resourceName);

			IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource
					.getCapabilityByInterface(IOpenflowForwardingCapability.class);

			FloodlightOFFlow flow = generateOFFlow(sdnNetworkOFFlow, source, destination, lastLink);

			forwardingCapability.createOpenflowForwardingRule(flow);

		}
	}

	private FloodlightOFFlow generateOFFlow(SDNNetworkOFFlow sdnNetworkOFFlow, Port source, Port destination, boolean lastLink)
			throws ActionException {

		FloodlightOFFlow flow = new FloodlightOFFlow(sdnNetworkOFFlow, null);
		flow.getMatch().setIngressPort(source.getPortNumber());

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

		return flow;
	}

	private IResource getResourceByName(String resourceName) throws ActivatorException, ResourceException {
		IResourceManager resourceManager = Activator.getResourceManagerService();
		IResourceIdentifier resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName);

		return resourceManager.getResource(resourceId);
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
