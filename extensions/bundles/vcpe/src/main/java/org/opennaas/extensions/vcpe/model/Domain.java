package org.opennaas.extensions.vcpe.model;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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

import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ IPNetworkDomain.class })
public class Domain extends VCPENetworkElement {

	@XmlIDREF
	protected List<Interface>	interfaces;

	public List<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Domain other = (Domain) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;
		return true;
	}

}
