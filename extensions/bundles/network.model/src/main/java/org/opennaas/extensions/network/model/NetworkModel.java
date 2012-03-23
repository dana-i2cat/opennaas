package org.opennaas.extensions.network.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.network.model.topology.NetworkElement;

import org.opennaas.core.resources.IModel;

public class NetworkModel implements IModel {

	private static final long	serialVersionUID	= -8240103035074766194L;

	List<NetworkElement>		networkElements		= new ArrayList<NetworkElement>();

	ResourcesReferences resourceReferences = new ResourcesReferences();

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	public List<NetworkElement> getNetworkElements() {
		return networkElements;
	}

	public void setNetworkElements(List<NetworkElement> networkElements) {
		this.networkElements = networkElements;
	}

	public String addResourceRef(String resourceName, String mantychoreResourceId){
		return resourceReferences.put(resourceName, mantychoreResourceId);
	}

	public String removeResourceRef(String resourceName){
		return resourceReferences.remove(resourceName);
	}

	public String getResourceRef(String resourceName){
		return resourceReferences.get(resourceName);
	}

	public ResourcesReferences getResourceReferences() {
		return resourceReferences;
	}

	public void setResourceReferences(ResourcesReferences resourceReferences) {
		this.resourceReferences = resourceReferences;
	}
}
