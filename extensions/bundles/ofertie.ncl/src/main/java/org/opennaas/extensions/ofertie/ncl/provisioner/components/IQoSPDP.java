package org.opennaas.extensions.ofertie.ncl.provisioner.components;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

public interface IQoSPDP {

	public boolean shouldAcceptRequest(String userId, QosPolicyRequest qosPolicyRequest) throws Exception;

}
