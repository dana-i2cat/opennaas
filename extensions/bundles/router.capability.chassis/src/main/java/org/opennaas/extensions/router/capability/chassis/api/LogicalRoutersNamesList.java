package org.opennaas.extensions.router.capability.chassis.api;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper class containing Logical Router names list
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "logicalRouters")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogicalRoutersNamesList {

	@XmlElement(name = "logicalRouter")
	private List<String>	logicalRouters;

	public List<String> getLogicalRoutersNames() {
		return logicalRouters;
	}

	public void setLogicalRouters(List<String> logicalRouters) {
		this.logicalRouters = logicalRouters;
	}

}
