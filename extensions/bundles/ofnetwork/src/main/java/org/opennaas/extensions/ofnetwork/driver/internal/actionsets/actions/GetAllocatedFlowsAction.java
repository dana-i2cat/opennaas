package org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.OFProvisioningNetworkActionSet;
import org.opennaas.extensions.ofnetwork.model.OFNetworkModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class GetAllocatedFlowsAction extends Action {

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// no params needed
		return true;
	}

	// FIXME: capability should read this information from underlying devices each time it is asked for it.
	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException {
		ActionResponse response = ActionResponse.okResponse(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);
		response.setResult(((OFNetworkModel) getModelToUpdate()).getNetFlowsPerResource());
		response.setStatus(STATUS.OK);
		return response;
	}

	// TODO: CORRECT WAY:
	// Retrieves live information from underlying devices
	// Requires information about which resources are part of the network

	// @Override
	// public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
	// throws ActionException {
	//
	// List<IResource> resources = getResourcesInNetwork();
	//
	// Map<String, List<NetOFFlow>> netFlowsPerResource = new HashMap<String, List<NetOFFlow>>();
	//
	// for (IResource resource : resources) {
	// List<NetOFFlow> netFlows = retrieveFlowsPerResource(resource);
	// netFlowsPerResource.put(resource.getResourceDescriptor().getInformation().getName(), netFlows);
	// }
	//
	// ActionResponse response = ActionResponse.okResponse(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);
	// response.setResult(netFlowsPerResource);
	// response.setStatus(STATUS.OK);
	// return response;
	// }
	//
	// public List<NetOFFlow> retrieveFlowsPerResource(IResource resource) throws CapabilityException {
	// IOpenflowForwardingCapability forwardingCapability;
	// try {
	// forwardingCapability = (IOpenflowForwardingCapability) resource
	// .getCapabilityByInterface(IOpenflowForwardingCapability.class);
	// } catch (ResourceException e) {
	// log.debug("Ignoring resource not having IOpenflowForwardingCapability capability. ResourceId: " +
	// resource.getResourceIdentifier().getId());
	// return new ArrayList<NetOFFlow>(0);
	// }
	//
	// List<FloodlightOFFlow> flows = forwardingCapability.getOpenflowForwardingRules();
	// List<NetOFFlow> netFlows = new ArrayList<NetOFFlow>(flows.size());
	//
	// for (FloodlightOFFlow obtainedFlow : flows) {
	// netFlows.add(new NetOFFlow(obtainedFlow, resource.getResourceDescriptor().getInformation().getName()));
	// }
	//
	// return netFlows;
	// }
	//
	// private List<IResource> getResourcesInNetwork() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
