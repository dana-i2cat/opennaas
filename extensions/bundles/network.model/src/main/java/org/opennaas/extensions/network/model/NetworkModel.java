package org.opennaas.extensions.network.model;

/*
 * #%L
 * OpenNaaS :: Network :: Model
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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.network.model.topology.NetworkElement;

public class NetworkModel implements IModel {

	private static final long	serialVersionUID	= -8240103035074766194L;

	List<NetworkElement>		networkElements		= new ArrayList<NetworkElement>();

	ResourcesReferences			resourceReferences	= new ResourcesReferences();

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

	public String addResourceRef(String resourceName, String mantychoreResourceId) {
		return resourceReferences.put(resourceName, mantychoreResourceId);
	}

	public String removeResourceRef(String resourceName) {
		return resourceReferences.remove(resourceName);
	}

	public String getResourceRef(String resourceName) {
		return resourceReferences.get(resourceName);
	}

	public ResourcesReferences getResourceReferences() {
		return resourceReferences;
	}

	public void setResourceReferences(ResourcesReferences resourceReferences) {
		this.resourceReferences = resourceReferences;
	}

	@Override
	public String toXml() throws SerializationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
