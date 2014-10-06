package org.opennaas.extensions.network.capability.topology.api;

/*
 * #%L
 * OpenNaaS :: Network :: Topology Discovery capability
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
 * Link model class
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlRootElement(name = "link", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {

	private Port	from;

	private Port	to;

	public Port getFrom() {
		return from;
	}

	public void setFrom(Port from) {
		this.from = from;
	}

	public Port getTo() {
		return to;
	}

	public void setTo(Port to) {
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode()) + ((to == null) ? 0 : to.hashCode());
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
		Link other = (Link) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		}
		if (to == null) {
			if (other.to != null)
				return false;
		}
		// from and to are interchangeable
		else if (from.equals(other.from) && to.equals(other.to) || from.equals(other.to) && to.equals(other.from)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Link [from=" + from + ", to=" + to + "]";
	}

}
