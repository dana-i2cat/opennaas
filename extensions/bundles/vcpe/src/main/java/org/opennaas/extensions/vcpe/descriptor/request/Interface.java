package org.opennaas.extensions.vcpe.descriptor.request;

public class Interface extends RequestElement {

	private String	name;

	private String	physicalInterfaceName;

	private long	vlanId;

	private String	ipAddress;

	private Link	linkTo;

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

	public Link getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(Link linkTo) {
		this.linkTo = linkTo;
	}
}
