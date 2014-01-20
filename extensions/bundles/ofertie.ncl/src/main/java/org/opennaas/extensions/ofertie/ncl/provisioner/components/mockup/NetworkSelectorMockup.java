package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

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

import org.opennaas.core.resources.IResourceManager;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;
import org.opennaas.extensions.ofnetwork.repository.OFNetworkRepository;

public class NetworkSelectorMockup implements INetworkSelector {

	private IResourceManager	resourceManager;

	/**
	 * @return the resourceManager
	 */
	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * @param resourceManager
	 *            the resourceManager to set
	 */
	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public String findNetworkForRequest(QosPolicyRequest qosPolicyRequest)
			throws Exception {
		return getFirstSDNNetworkInOpenNaaS();
	}

	@Override
	public String findNetworkForFlowId(String flowId) throws Exception {
		return getFirstSDNNetworkInOpenNaaS();
	}

	/**
	 * It assumes OpenNaaS supports SDN networks and there is at least one of them active.
	 * 
	 * @return
	 */
	private String getFirstSDNNetworkInOpenNaaS() {
		return resourceManager.listResourcesByType(OFNetworkRepository.OF_NETWORK_RESOURCE_TYPE).get(0)
				.getResourceIdentifier().getId();
	}

}
