package org.opennaas.extensions.ofertie.ncl.notification;

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
