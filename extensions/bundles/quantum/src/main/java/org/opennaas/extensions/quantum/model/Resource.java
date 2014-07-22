package org.opennaas.extensions.quantum.model;

/*
 * #%L
 * OpenNaaS :: Quantum
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * The Resource class is used to link Quantum resource with the OpenNaaS resources it uses to provide the network.
 * 
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource {

	protected String				resourceId;
	protected List<ResourceElement>	resourceElement;

	public Resource() {
		resourceElement = new ArrayList<ResourceElement>();
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public List<ResourceElement> getResourceElement() {
		return resourceElement;
	}

	public void setResourceElement(List<ResourceElement> resourceElement) {
		this.resourceElement = resourceElement;
	}

	public void addResourceElement(ResourceElement resourceElement) {
		this.getResourceElement().add(resourceElement);
	}

	public void removeResourceElement(ResourceElement resourceElement) {
		this.getResourceElement().remove(resourceElement);
	}

}
