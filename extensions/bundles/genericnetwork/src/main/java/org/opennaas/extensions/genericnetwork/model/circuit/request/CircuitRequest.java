package org.opennaas.extensions.genericnetwork.model.circuit.request;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"source",
		"destination",
		"label",
		"qosPolicy"
})
@XmlRootElement(name = "qos_policy_request", namespace = "opennaas.api")
public class CircuitRequest {

	@XmlAttribute(name = "atomic")
	@XmlJavaTypeAdapter(NormalizedStringAdapter.class)
	private String		atomic;
	@XmlElement(required = true)
	private Source		source;
	@XmlElement(required = true)
	private Destination	destination;
	@XmlElement(required = true)
	private String		label;
	@XmlElement(name = "qos_policy")
	private QoSPolicy	qosPolicy;

	public String getAtomic() {
		return atomic;
	}

	public void setAtomic(String atomic) {
		this.atomic = atomic;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public QoSPolicy getQosPolicy() {
		return qosPolicy;
	}

	public void setQosPolicy(QoSPolicy qosPolicy) {
		this.qosPolicy = qosPolicy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atomic == null) ? 0 : atomic.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((qosPolicy == null) ? 0 : qosPolicy.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		CircuitRequest other = (CircuitRequest) obj;
		if (atomic == null) {
			if (other.atomic != null)
				return false;
		} else if (!atomic.equals(other.atomic))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (qosPolicy == null) {
			if (other.qosPolicy != null)
				return false;
		} else if (!qosPolicy.equals(other.qosPolicy))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

}
