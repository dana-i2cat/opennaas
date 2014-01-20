package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IQoSPDP;

public class QoSPDPMockup implements IQoSPDP {

	/**
	 * Accepts all requests
	 */
	@Override
	public boolean shouldAcceptRequest(String userId, QosPolicyRequest qosPolicyRequest)
			throws Exception {
		return true;
	}

}
