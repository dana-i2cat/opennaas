package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;

public interface IQoSPDP {

	public boolean shouldAcceptRequest(String userId, FlowRequest flowRequest) throws Exception;

}
