package org.opennaas.extensions.ofertie.ncl.controller;

import java.util.List;
import java.util.Set;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.ofertie.ncl.Activator;
import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowRetrievalException;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class NCLController implements INCLController {

	@Override
	public void allocateFlows(List<NetOFFlow> flows, String networkId) throws FlowAllocationException {
		try {
			IResource networkResource = getResource(networkId);
			IOFProvisioningNetworkCapability provisionCapab = (IOFProvisioningNetworkCapability) networkResource
					.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

			provisionCapab.allocateFlows(flows);

		} catch (ActivatorException e) {
			throw new FlowAllocationException(e);
		} catch (ResourceException e) {
			throw new FlowAllocationException(e);
		}
	}

	@Override
	public void deallocateFlows(List<NetOFFlow> flows, String networkId) throws FlowAllocationException {
		try {
			IResource networkResource = getResource(networkId);
			IOFProvisioningNetworkCapability provisionCapab = (IOFProvisioningNetworkCapability) networkResource
					.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

			provisionCapab.deallocateFlows(flows);

		} catch (ActivatorException e) {
			throw new FlowAllocationException(e);
		} catch (ResourceException e) {
			throw new FlowAllocationException(e);
		}
	}

	@Override
	public Set<NetOFFlow> getAllocatedFlows(String networkId) throws FlowRetrievalException {
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

	@Override
	public void replaceFlows(List<NetOFFlow> current, List<NetOFFlow> desired, String networkId) throws FlowAllocationException {
		try {
			IResource networkResource = getResource(networkId);
			IOFProvisioningNetworkCapability provisionCapab = (IOFProvisioningNetworkCapability) networkResource
					.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

			provisionCapab.replaceFlows(current, desired);

		} catch (ActivatorException e) {
			throw new FlowAllocationException(e);
		} catch (ResourceException e) {
			throw new FlowAllocationException(e);
		}
	}

	private IResource getResource(String networkId) throws ActivatorException, ResourceException {

		IResourceManager resourceManager = Activator.getResourceManagerService();
		IResource resource = resourceManager.getResourceById(networkId);

		return resource;
	}

}
