package org.opennaas.extensions.ofertie.ncl.provisioner.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Defines a flow with its QoS network requirement.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FlowRequest {

	// TODO to be removed.
	// only required by PathFinderMockup.
	// Not used in equals and hashCode.
	private String			requestId;

	private String			sourceIPAddress;
	private String			destinationIPAddress;

	/**
	 * TCP/UDP/SCTP source port
	 */
	private int				sourcePort;
	/**
	 * TCP/UDP/SCTP destination port
	 */
	private int				destinationPort;

	/**
	 * Type of Service (ToS) value
	 */
	private int				tos;

	private int				sourceVlanId;
	private int				destinationVlanId;

	private QoSRequirements	qoSRequirements;

	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId
	 *            the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the sourceIPAddress
	 */
	public String getSourceIPAddress() {
		return sourceIPAddress;
	}

	/**
	 * @param sourceIPAddress
	 *            the sourceIPAddress to set
	 */
	public void setSourceIPAddress(String sourceIPAddress) {
		this.sourceIPAddress = sourceIPAddress;
	}

	/**
	 * @return the destinationIPAddress
	 */
	public String getDestinationIPAddress() {
		return destinationIPAddress;
	}

	/**
	 * @param destinationIPAddress
	 *            the destinationIPAddress to set
	 */
	public void setDestinationIPAddress(String destinationIPAddress) {
		this.destinationIPAddress = destinationIPAddress;
	}

	/**
	 * @return the sourcePort
	 */
	public int getSourcePort() {
		return sourcePort;
	}

	/**
	 * @param sourcePort
	 *            the sourcePort to set
	 */
	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	/**
	 * @return the destinationPort
	 */
	public int getDestinationPort() {
		return destinationPort;
	}

	/**
	 * @param destinationPort
	 *            the destinationPort to set
	 */
	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}

	/**
	 * @return the tos
	 */
	public int getTos() {
		return tos;
	}

	/**
	 * @param tos
	 *            the tos to set
	 */
	public void setTos(int tos) {
		this.tos = tos;
	}

	/**
	 * @return the sourceVlanId
	 */
	public int getSourceVlanId() {
		return sourceVlanId;
	}

	/**
	 * @param sourceVlanId
	 *            the sourceVlanId to set
	 */
	public void setSourceVlanId(int sourceVlanId) {
		this.sourceVlanId = sourceVlanId;
	}

	/**
	 * @return the destinationVlanId
	 */
	public int getDestinationVlanId() {
		return destinationVlanId;
	}

	/**
	 * @param destinationVlanId
	 *            the destinationVlanId to set
	 */
	public void setDestinationVlanId(int destinationVlanId) {
		this.destinationVlanId = destinationVlanId;
	}

	/**
	 * @return the qoSRequirements
	 */
	public QoSRequirements getQoSRequirements() {
		return qoSRequirements;
	}

	/**
	 * @param qoSRequirements
	 *            the qoSRequirements to set
	 */
	public void setQoSRequirements(QoSRequirements qoSRequirements) {
		this.qoSRequirements = qoSRequirements;
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
		result = prime
				* result
				+ ((destinationIPAddress == null) ? 0 : destinationIPAddress
						.hashCode());
		result = prime * result + destinationPort;
		result = prime * result + destinationVlanId;
		result = prime * result
				+ ((qoSRequirements == null) ? 0 : qoSRequirements.hashCode());
		result = prime * result
				+ ((sourceIPAddress == null) ? 0 : sourceIPAddress.hashCode());
		result = prime * result + sourcePort;
		result = prime * result + sourceVlanId;
		result = prime * result + tos;
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
		FlowRequest other = (FlowRequest) obj;
		if (destinationIPAddress == null) {
			if (other.destinationIPAddress != null)
				return false;
		} else if (!destinationIPAddress.equals(other.destinationIPAddress))
			return false;
		if (destinationPort != other.destinationPort)
			return false;
		if (destinationVlanId != other.destinationVlanId)
			return false;
		if (qoSRequirements == null) {
			if (other.qoSRequirements != null)
				return false;
		} else if (!qoSRequirements.equals(other.qoSRequirements))
			return false;
		if (sourceIPAddress == null) {
			if (other.sourceIPAddress != null)
				return false;
		} else if (!sourceIPAddress.equals(other.sourceIPAddress))
			return false;
		if (sourcePort != other.sourcePort)
			return false;
		if (sourceVlanId != other.sourceVlanId)
			return false;
		if (tos != other.tos)
			return false;
		return true;
	}

}
