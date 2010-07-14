package net.i2cat.mantychore.models.router;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

public class SubInterface {
	@NotBlank
	protected String			mtu;
	/**
	 * Parameter NOT used
	 */
	protected String			description;
	@NotBlank
	protected String			identifier;
	@NotBlank
	protected String			speed;
	@NotBlank
	protected String			encapsulation;
	@NotBlank
	protected String			physicalInterfaceID;
	@NotBlank
	protected IPConfiguration	iPconfiguration;
	/**
	 * Must be configured when the interface is physical
	 */
	protected String			vlanID;
	/**
	 * Must be configured when the interface is logical
	 */
	protected String			peerUnit;

	public String getMtu() {
		return mtu;
	}

	public void setMtu(String value) {
		this.mtu = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String value) {
		this.identifier = value;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String value) {
		this.speed = value;
	}

	public String getEncapsulation() {
		return encapsulation;
	}

	public void setEncapsulation(String value) {
		this.encapsulation = value;
	}

	public String getPhysicalInterfaceID() {
		return physicalInterfaceID;
	}

	public void setPhysicalInterfaceID(String value) {
		this.physicalInterfaceID = value;
	}

	public IPConfiguration getIPconfiguration() {
		return iPconfiguration;
	}

	public void setIPconfiguration(IPConfiguration value) {
		this.iPconfiguration = value;
	}

	public String getVlanID() {
		return vlanID;
	}

	public void setVlanID(String value) {
		this.vlanID = value;
	}

	public String getPeerUnit() {
		return peerUnit;
	}

	public void setPeerUnit(String value) {
		this.peerUnit = value;
	}

}
