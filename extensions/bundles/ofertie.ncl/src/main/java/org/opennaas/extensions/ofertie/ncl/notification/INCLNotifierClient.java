package org.opennaas.extensions.ofertie.ncl.notification;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

public interface INCLNotifierClient {

	public void qosPolicyAllocated(String flowId, QosPolicyRequest request); // Sends a PUT notification

	public void qosPolicyAllocationFailed(String flowId, Exception error); // Sends a DELETE notification with 501 code

	public void flowDeleted(String flowId); // Sends a DELETE notification with 201 code
}
