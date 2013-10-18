package org.opennaas.extensions.ofertie.ncl.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.helpers.FlowRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class NCLController implements INCLController {

	private Map<String, Flow>	allocatedFlows;

	public NCLController() {
		allocatedFlows = new HashMap<String, Flow>();
	}

	@Override
	public String allocateFlow(FlowRequest flowRequest, Route route,
			String networkId) throws FlowAllocationException {

		try {

			SDNNetworkOFFlow flowWithRoute = FlowRequestParser.parseFlowRequestIntoSDNFlow(flowRequest, route);
			flowWithRoute.setActive(true);
			flowWithRoute.setPriority("32768");
			// FIXME requesting a flow that won't filter by IP, by now
			flowWithRoute.getMatch().setSrcIp(null);
			flowWithRoute.getMatch().setDstIp(null);
			// FIXME requesting a flow that won't filter by ToS, by now
			flowWithRoute.getMatch().setTosBits(null);

			IResource networkResource = getResource(networkId);
			IOFProvisioningNetworkCapability provisionCapab = (IOFProvisioningNetworkCapability) networkResource
					.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

			String flowId = provisionCapab.allocateOFFlow(flowWithRoute);

			Flow flow = new Flow();
			flow.setFlowRequest(flowRequest);
			flow.setId(flowId);
			allocatedFlows.put(flowId, flow);

			return flowId;

		} catch (ActivatorException e) {
			throw new FlowAllocationException(e);
		} catch (ResourceException e) {
			throw new FlowAllocationException(e);
		}
	}

	@Override
	public String deallocateFlow(String flowId, String networkId) throws FlowAllocationException {

		try {
			IResource networkResource = getResource(networkId);
			IOFProvisioningNetworkCapability provisionCapab = (IOFProvisioningNetworkCapability) networkResource
					.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

			provisionCapab.deallocateOFFlow(flowId);
			allocatedFlows.remove(flowId);
			return flowId;

		} catch (ActivatorException e) {
			throw new FlowAllocationException(e);
		} catch (ResourceException e) {
			throw new FlowAllocationException(e);
		}
	}

	@Override
	public Collection<Flow> getFlows() {
		return allocatedFlows.values();
	}

	private IResource getResource(String networkId) throws ActivatorException, ResourceException {

		IResourceManager resourceManager = Activator.getResourceManagerService();
		IResource resource = resourceManager.getResourceById(networkId);

		return resource;
	}
}
