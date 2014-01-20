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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * mixin of a route
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Route {

	private String	destination;
	private String	nexthop;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getNexthop() {
		return nexthop;
	}

	public void setNexthop(String nexthop) {
		this.nexthop = nexthop;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (!destination.equals(other.getDestination()))
			return false;
		if (!nexthop.equals(other.getNexthop()))
			return false;
		return true;
	}
}
