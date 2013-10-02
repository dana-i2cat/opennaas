package org.opennaas.extensions.ofertie.ncl.provisioner.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Flow {

	private String		id;

	private FlowRequest	flowRequest;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the request
	 */
	public FlowRequest getFlowRequest() {
		return flowRequest;
	}

	/**
	 * @param request
	 *            the request to set
	 */
	public void setFlowRequest(FlowRequest flowRequest) {
		this.flowRequest = flowRequest;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((flowRequest == null) ? 0 : flowRequest.hashCode());
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
		Flow other = (Flow) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (flowRequest == null) {
			if (other.flowRequest != null)
				return false;
		} else if (!flowRequest.equals(other.flowRequest))
			return false;
		return true;
	}

}
