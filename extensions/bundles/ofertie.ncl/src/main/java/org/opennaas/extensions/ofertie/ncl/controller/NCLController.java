package org.opennaas.extensions.ofertie.ncl.controller;

import java.util.Collection;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowRetrievalException;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class NCLController implements INCLController {

	@Override
	public String allocateFlow(SDNNetworkOFFlow flowWithRoute, String networkId) throws FlowAllocationException {
		try {

			IResource networkResource = getResource(networkId);
			IOFProvisioningNetworkCapability provisionCapab = (IOFProvisioningNetworkCapability) networkResource
					.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

			return provisionCapab.allocateOFFlow(flowWithRoute);

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
			return flowId;

		} catch (ActivatorException e) {
			throw new FlowAllocationException(e);
		} catch (ResourceException e) {
			throw new FlowAllocationException(e);
		}
	}

	@Override
	public Collection<SDNNetworkOFFlow> getFlows(String networkId) throws FlowRetrievalException {

		try {
			IResource networkResource = getResource(networkId);
			IOFProvisioningNetworkCapability provisionCapab = (IOFProvisioningNetworkCapability) networkResource
					.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

			return provisionCapab.getAllocatedFlows();

		} catch (ActivatorException e) {
			throw new FlowRetrievalException(e);
		} catch (ResourceException e) {
			throw new FlowRetrievalException(e);
		}
	}

	private IResource getResource(String networkId) throws ActivatorException, ResourceException {

		IResourceManager resourceManager = Activator.getResourceManagerService();
		IResource resource = resourceManager.getResourceById(networkId);

		return resource;
	}
}
