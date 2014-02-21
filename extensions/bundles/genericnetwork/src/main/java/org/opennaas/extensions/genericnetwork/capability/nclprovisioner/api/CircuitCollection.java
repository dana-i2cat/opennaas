package org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api;

/*
 * #%L
 * OpenNaaS :: Generic Network
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
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(name = "circuits", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class CircuitCollection implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8144269409692480588L;

	@XmlElement(name = "circuit")
	private Collection<Circuit>	circuits;

	public Collection<Circuit> getCircuits() {
		return circuits;
	}

	public void setCircuits(Collection<Circuit> circuits) {
		this.circuits = circuits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((circuits == null) ? 0 : circuits.hashCode());
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
		CircuitCollection other = (CircuitCollection) obj;
		if (circuits == null) {
			if (other.circuits != null)
				return false;
		} else if (!circuits.equals(other.circuits))
			return false;
		return true;
	}

}
