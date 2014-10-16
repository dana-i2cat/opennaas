package org.opennaas.extensions.ofertie.ncl.notification;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

public class NCLNotifierClient implements INCLNotifierClient {

	@Override
	public void qosPolicyAllocated(String flowId, QosPolicyRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void qosPolicyAllocationFailed(String flowId, Exception error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flowDeleted(String flowId) {
		// TODO Auto-generated method stub

	}

}
