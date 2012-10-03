package org.opennaas.extensions.vcpe.descriptor.request;

import javax.xml.bind.annotation.XmlIDREF;

public class RequestInterface extends RequestElement {

	private String		name;

	private String		physicalInterfaceName;

	private long		vlanId;

	private String		ipAddress;

	@XmlIDREF
	private RequestLink	linkTo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhysicalInterfaceName() {
		return physicalInterfaceName;
	}

	public void setPhysicalInterfaceName(String physicalInterfaceName) {
		this.physicalInterfaceName = physicalInterfaceName;
	}

	public long getVlanId() {
		return vlanId;
	}

	public void setVlanId(long vlanId) {
		this.vlanId = vlanId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public RequestLink getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(RequestLink linkTo) {
		this.linkTo = linkTo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + ((linkTo == null) ? 0 : linkTo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((physicalInterfaceName == null) ? 0 : physicalInterfaceName.hashCode());
		result = prime * result + (int) (vlanId ^ (vlanId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestInterface other = (RequestInterface) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (linkTo == null) {
			if (other.linkTo != null)
				return false;
		} else if (!linkTo.equals(other.linkTo))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (physicalInterfaceName == null) {
			if (other.physicalInterfaceName != null)
				return false;
		} else if (!physicalInterfaceName.equals(other.physicalInterfaceName))
			return false;
		if (vlanId != other.vlanId)
			return false;
		return true;
	}

}
