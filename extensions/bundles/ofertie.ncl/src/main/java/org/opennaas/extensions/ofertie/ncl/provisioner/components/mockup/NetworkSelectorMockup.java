package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

import org.opennaas.core.resources.IResourceManager;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.INetworkSelector;

public class NetworkSelectorMockup implements INetworkSelector {
	
	private static final String SDN_NETWORK_RESOURCE_TYPE= "sdnnet";
	private IResourceManager resourceManager;
	
	/**
	 * @return the resourceManager
	 */
	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * @param resourceManager the resourceManager to set
	 */
	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public String findNetworkForRequest(FlowRequest flowRequest)
			throws Exception {
		return getFirstSDNNetworkInOpenNaaS();
	}
	
	/**
	 * It assumes OpenNaaS supports SDN networks and there is at least one of them active.
	 * @return
	 */
	private String getFirstSDNNetworkInOpenNaaS() {
		return resourceManager.listResourcesByType(SDN_NETWORK_RESOURCE_TYPE).get(0)
				.getResourceIdentifier().getId();
	}

}