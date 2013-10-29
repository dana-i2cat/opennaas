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
				provisionLink(networkConnection, new SDNNetworkOFFlow(flow),
						i == connections.size() - 1);
			} catch (Exception e) {
				throw new ActionException("Error provisioning link : ", e);
			}
		}
		return ActionResponse.okResponse(getActionID());
	}

	/**
	 * private void provisionLink(Port source, Port destination, SDNNetworkOFFlow sdnNetworkOFFlow, boolean lastLink) throws ActionException { if
	 * (source.getDeviceId() != destination.getDeviceId()) { // link between different devices, assume it exists or it is provisioned } else { // link
	 * inside same device, use device internal capability to provision it // get OpenNaaS resource Id from the map in the model String deviceId =
	 * source.getDeviceId(); String resourceId = ((SDNNetworkModel) getModelToUpdate()).getDeviceResourceMap().get(deviceId); if (resourceId == null)
	 * { throw new ActionException("No resource Id found from device Id: " + deviceId); }
	 * 
	 * // get switch resource from Resource Manager using resource Id IResource resource = null; try { resource =
	 * Activator.getResourceManagerService().getResourceById(resourceId); } catch (Exception e) { throw new
	 * ActionException("Error getting switch resource from Resource Manager", e); }
	 * 
	 * // get IOpenflowForwardingCapability from the obtained resource IOpenflowForwardingCapability capability = null; try { capability =
	 * (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class); } catch (ResourceException e) { throw
	 * new ActionException("Error getting IOpenflowForwardingCapability from resource with Id: " + resourceId, e); }
	 * 
	 * // construct FloodlightOFFlow based on SDNNetworkOFFlow, source Port, destination Port and lastLink FloodlightOFFlow flow = new
	 * FloodlightOFFlow(sdnNetworkOFFlow, deviceId);
	 * 
	 * flow.getMatch().setIngressPort(source.getPortNumber());
	 * 
	 * // Only last link in the flow should apply actions other than forwarding. // The rest of the links should have only forwarding actions.
	 * List<FloodlightOFAction> actions = flow.getActions(); if (!lastLink) { FloodlightOFAction outputAction = null; for (FloodlightOFAction
	 * floodlightOFAction : actions) { if (floodlightOFAction.getType() == FloodlightOFAction.TYPE_OUTPUT) { outputAction = floodlightOFAction; } } if
	 * (outputAction == null) { throw new ActionException("No output action found in FloodlightOFFlow."); } // clear list and add output action
	 * actions.clear(); actions.add(outputAction); }
	 * 
	 * // invoke IOpenflowForwardingCapability try { capability.createOpenflowForwardingRule(flow); } catch (CapabilityException e) { throw new
	 * ActionException("Error executing IOpenflowForwardingCapability from resource with Id: " + resourceId, e); } } }
	 **/

	/**
	 * The commented code is the right way to do it, but since there's no way to set the mapping between the switchID and the deviceID and we have a
	 * demo tomorrow, we will take the switch resource from the ResourceManager. Every switch contains in its model its floodlight switchId, so we can
	 * use it as a work around.
	 * 
	 * @param source
	 * @param destination
	 * @param sdnNetworkOFFlow
	 * @param lastLink
	 * @throws ResourceException
	 * @throws ActivatorException
	 */
	private void provisionLink(NetworkConnection connection, SDNNetworkOFFlow sdnNetworkOFFlow, boolean isLastLinkInRoute) throws ResourceException,
			ActivatorException {

		if (connection.getSource().getDeviceId().equals(connection.getDestination().getDeviceId())) {
			/* link inside same device, use device internal capability to provision it */

			// Last link should include all actions of the original flow and the forwarding one.
			// The rest only the forwarding rule.
			// This way we can re-use original match in all links of the same flow.
			FloodlightOFFlow flow = generateOFFlow(connection, sdnNetworkOFFlow, isLastLinkInRoute);

			String resourceName = connection.getSource().getDeviceId();
			IResource resource = getResourceByName(resourceName);
			IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource
					.getCapabilityByInterface(IOpenflowForwardingCapability.class);

			forwardingCapability.createOpenflowForwardingRule(flow);
		} else {
			/* link between different devices, assume it exists or it is provisioned */
		}
	}

	/**
	 * 
	 * @param connection
	 * @param sdnNetworkOFFlow
	 * @param keepActions
	 *            whether returned flow must have actions in sdnNetworkOFFlow or not.
	 * @return a FloodlightOFFlow representing given connection in sdnNetworkOFFlow.
	 * @throws ActionException
	 */
	private FloodlightOFFlow generateOFFlow(NetworkConnection connection, SDNNetworkOFFlow sdnNetworkOFFlow, boolean keepActions)
			throws ActionException {

		Port source = connection.getSource();
		Port destination = connection.getDestination();

		FloodlightOFFlow flow = new FloodlightOFFlow(sdnNetworkOFFlow, null);
		flow.setName(connection.getId());
		flow.getMatch().setIngressPort(source.getPortNumber());

		FloodlightOFAction outputAction = new FloodlightOFAction();
		outputAction.setType(FloodlightOFAction.TYPE_OUTPUT);
		outputAction.setValue(destination.getPortNumber());

		// All links should include a forwarding action (outputAction)
		List<FloodlightOFAction> actions = flow.getActions();
		if (!keepActions) {
			actions.clear();
		} else {
			for (FloodlightOFAction floodlightOFAction : actions) {
				if (floodlightOFAction.getType() == FloodlightOFAction.TYPE_OUTPUT) {
					actions.remove(floodlightOFAction);
				}
			}
		}
		actions.add(outputAction);
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
