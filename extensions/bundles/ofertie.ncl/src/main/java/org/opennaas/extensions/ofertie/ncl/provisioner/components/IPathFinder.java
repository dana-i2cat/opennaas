package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Route;

public interface IPathFinder {
	
	public Route findPathForRequest(FlowRequest flowRequest, String networkId) throws Exception;

}
