package org.opennaas.extensions.genericnetwork.model.circuit;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class Circuit implements Serializable {

	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long	serialVersionUID	= -4888253800456186130L;

	private String				circuitId;
	private Route				route;
	private QoSPolicy			qos;
	private FloodlightOFMatch	trafficFilter;

	/**
	 * @return the circuitId
	 */
	public String getCircuitId() {
		return circuitId;
	}

	/**
	 * @param circuitId
	 *            the circuitId to set
	 */
	public void setCircuitId(String circuitId) {
		this.circuitId = circuitId;
	}

	/**
	 * @return the route
	 */
	public Route getRoute() {
		return route;
	}

	/**
	 * @param route
	 *            the route to set
	 */
	public void setRoute(Route route) {
		this.route = route;
	}

	/**
	 * @return the qos
	 */
	public QoSPolicy getQos() {
		return qos;
	}

	/**
	 * @param qos
	 *            the qos to set
	 */
	public void setQos(QoSPolicy qos) {
		this.qos = qos;
	}

	/**
	 * @return the trafficFilter
	 */
	public FloodlightOFMatch getTrafficFilter() {
		return trafficFilter;
	}

	/**
	 * @param trafficFilter
	 *            the trafficFilter to set
	 */
	public void setTrafficFilter(FloodlightOFMatch trafficFilter) {
		this.trafficFilter = trafficFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((circuitId == null) ? 0 : circuitId.hashCode());
		result = prime * result + ((qos == null) ? 0 : qos.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((trafficFilter == null) ? 0 : trafficFilter.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Circuit other = (Circuit) obj;
		if (circuitId == null) {
			if (other.circuitId != null)
				return false;
		} else if (!circuitId.equals(other.circuitId))
			return false;
		if (qos == null) {
			if (other.qos != null)
				return false;
		} else if (!qos.equals(other.qos))
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (trafficFilter == null) {
			if (other.trafficFilter != null)
				return false;
		} else if (!trafficFilter.equals(other.trafficFilter))
			return false;
		return true;
	}

}
