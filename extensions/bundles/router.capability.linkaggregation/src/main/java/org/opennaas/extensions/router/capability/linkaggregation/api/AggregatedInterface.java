package org.opennaas.extensions.router.capability.linkaggregation.api;

/*
 * #%L
 * OpenNaaS :: Router :: Link Aggregation Capability
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

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Aggregated Interface wrapper class
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "aggregatedInterface", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class AggregatedInterface {

	String				id;

	@XmlElementWrapper(name = "interfaces")
	@XmlElement(name = "interface")
	List<String>		interfacesNames;

	@XmlElementWrapper(name = "aggregationOptions")
	Map<String, String>	aggregationOptions;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getInterfacesNames() {
		return interfacesNames;
	}

	public void setInterfacesNames(List<String> interfacesNames) {
		this.interfacesNames = interfacesNames;
	}

	public Map<String, String> getAggregationOptions() {
		return aggregationOptions;
	}

	public void setAggregationOptions(Map<String, String> aggregationOptions) {
		this.aggregationOptions = aggregationOptions;
	}

}
