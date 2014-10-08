package org.opennaas.extensions.openflowswitch.model;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
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

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(name = "floodlightOFFlows")
@XmlAccessorType(XmlAccessType.FIELD)
public class FloodlightOFFlowListWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 6469082812844530401L;

	@XmlElementWrapper(name = "floodlightOFFlows")
	private List<FloodlightOFFlow>	forwardingRules;

	public List<FloodlightOFFlow> getForwardingRules() {
		return forwardingRules;
	}

	public void setForwardingRules(List<FloodlightOFFlow> forwardingRules) {
		this.forwardingRules = forwardingRules;
	}

	@Override
	public String toString() {
		return "ForwardingRuleLisWrapper [forwardingRules=" + forwardingRules + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((forwardingRules == null) ? 0 : forwardingRules.hashCode());
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
		FloodlightOFFlowListWrapper other = (FloodlightOFFlowListWrapper) obj;
		if (forwardingRules == null) {
			if (other.forwardingRules != null)
				return false;
		} else if (!forwardingRules.equals(other.forwardingRules))
			return false;
		return true;
	}
}
