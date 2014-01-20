package org.opennaas.extensions.ofertie.ncl.controller;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
