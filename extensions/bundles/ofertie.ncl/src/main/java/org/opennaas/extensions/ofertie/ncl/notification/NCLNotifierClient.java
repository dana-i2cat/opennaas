package org.opennaas.extensions.ofertie.ncl.notification;

import org.opennaas.extensions.ofertie.ncl.notification.api.INCLNotificationAPI;
import org.opennaas.extensions.ofertie.ncl.notification.api.NCLNotificationAPIClientFactory;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

public class NCLNotifierClient implements INCLNotifierClient {

	private INCLNotificationAPI	nclNotificationAPI;

	public NCLNotifierClient(String baseURL) {
		nclNotificationAPI = NCLNotificationAPIClientFactory.createClient(baseURL);
	}

	@Override
	public void qosPolicyAllocated(String flowId, QosPolicyRequest request) {
		nclNotificationAPI.flowCreated(flowId, request.getQosPolicy());
	}

	@Override
	public void qosPolicyAllocationFailed(String flowId, QosPolicyRequest request, Exception error) {
		nclNotificationAPI.flowRejected(flowId, request.getQosPolicy());
	}

	@Override
	public void flowDeleted(String flowId, QosPolicyRequest request) {
		nclNotificationAPI.flowRejected(flowId, request.getQosPolicy());
	}

}
