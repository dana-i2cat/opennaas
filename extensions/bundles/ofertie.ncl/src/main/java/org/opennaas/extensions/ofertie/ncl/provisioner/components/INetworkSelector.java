package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;

public interface INetworkSelector {

	public String findNetworkForRequest(FlowRequest flowRequest) throws Exception;

}
