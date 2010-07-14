package net.i2cat.mantychore.models.router;

import java.util.ArrayList;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

public class PhysicalInterface {
	@NotBlank
	protected String				status;
	@NotBlank
	protected String				linkStatus;
	@NotBlank
	protected int					keepalive;
	@NotBlank
	protected String				macAddress;
	@NotBlank
	protected String				type;
	/**
	 * Indicate the name of the interface
	 */
	@NotBlank
	protected String				location;
	@NotBlank
	protected String				linkMode;
	protected List<SubInterface>	subInterfaces;
	protected PhysicalInterface		physicalInterfaceParent;

	public String getStatus() {
		return status;
	}

	public void setStatus(String value) {
		this.status = value;
	}

	public String getLinkStatus() {
		return linkStatus;
	}

	public void setLinkStatus(String value) {
		this.linkStatus = value;
	}

	public int getKeepalive() {
		return keepalive;
	}

	public void setKeepalive(int value) {
		this.keepalive = value;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String value) {
		this.macAddress = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String value) {
		this.type = value;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String value) {
		this.location = value;
	}

	public String getLinkMode() {
		return linkMode;
	}

	public void setLinkMode(String value) {
		this.linkMode = value;
	}

	public List<SubInterface> getSubInterfaces() {
		if (subInterfaces == null) {
			subInterfaces = new ArrayList<SubInterface>();
		}
		return this.subInterfaces;
	}

	public PhysicalInterface getPhysicalInterfaceParent() {
		return physicalInterfaceParent;
	}

	public void setPhysicalInterfaceParent(PhysicalInterface value) {
		this.physicalInterfaceParent = value;
	}

}
