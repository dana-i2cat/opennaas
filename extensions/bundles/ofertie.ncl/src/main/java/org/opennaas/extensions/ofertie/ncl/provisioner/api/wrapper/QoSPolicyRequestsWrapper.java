package org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper;

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
