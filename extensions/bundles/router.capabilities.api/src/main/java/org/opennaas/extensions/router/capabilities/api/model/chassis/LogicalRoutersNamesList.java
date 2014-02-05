package org.opennaas.extensions.router.capabilities.api.model.chassis;

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

	public List<String> getLogicalRouters() {
		return logicalRouters;
	}

	public void setLogicalRouters(List<String> logicalRouters) {
		this.logicalRouters = logicalRouters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((logicalRouters == null) ? 0 : logicalRouters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogicalRoutersNamesList other = (LogicalRoutersNamesList) obj;
		if (logicalRouters == null) {
			if (other.logicalRouters != null)
				return false;
		} else if (!logicalRouters.equals(other.logicalRouters))
			return false;
		return true;
	}

}
