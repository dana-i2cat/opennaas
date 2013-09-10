package org.opennaas.extensions.ofertie.ncl.controller;

import org.opennaas.extensions.ofertie.ncl.controller.api.INCLController;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Route;

public class NCLController implements INCLController {

	@Override
	public String allocateFlow(FlowRequest flowRequest, Route route,
			String networkId) throws FlowAllocationException {
		
		// TODO retrieve SDN network from OpenNaaS using networkId
		// TODO delegate to its OpenflowProvisioningNetworkCapability
		// TODO obtain flowId from result and return it
		
		return String.valueOf(flowRequest.getTos());
	}

}
