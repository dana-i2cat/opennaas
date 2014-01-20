package org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QoSPolicyRequestsWrapper {

	private Map<String, QosPolicyRequest>	qoSPolicyRequests;

	public QoSPolicyRequestsWrapper() {
		qoSPolicyRequests = new HashMap<String, QosPolicyRequest>();
	}

	public QoSPolicyRequestsWrapper(Map<String, QosPolicyRequest> qoSPolicyRequests) {
		super();
		this.qoSPolicyRequests = qoSPolicyRequests;
	}

	/**
	 * @return the qoSPolicyRequests
	 */
	public Map<String, QosPolicyRequest> getQoSPolicyRequests() {
		return qoSPolicyRequests;
	}

	/**
	 * @param qoSPolicyRequests
	 *            the qoSPolicyRequests to set
	 */
	public void setQoSPolicyRequests(Map<String, QosPolicyRequest> qoSPolicyRequests) {
		this.qoSPolicyRequests = qoSPolicyRequests;
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
		result = prime * result + ((qoSPolicyRequests == null) ? 0 : qoSPolicyRequests.hashCode());
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
		QoSPolicyRequestsWrapper other = (QoSPolicyRequestsWrapper) obj;
		if (qoSPolicyRequests == null) {
			if (other.qoSPolicyRequests != null)
				return false;
		} else if (!qoSPolicyRequests.equals(other.qoSPolicyRequests))
			return false;
		return true;
	}

}
