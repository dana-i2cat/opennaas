package net.i2cat.mantychore.models.router;

import java.util.ArrayList;
import java.util.List;

public class PhysicalInterface {

	protected String				status;

	protected String				linkStatus;

	protected int					keepalive;

	protected String				macAddress;

	protected String				type;
	/**
	 * Indicate the name of the interface
	 */

	protected String				location;

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

	public void addSubInterface(SubInterface subInterface) {
		// TODO it is necessary to specify which it is an identifier
		// for (SubInterface bucleSubInterf : subInterfaces) {
		// if
		// (bucleSubInterf.getIdentifier().equals(subInterface.getIdentifier()))
		// {
		// return;
		// }
		// }
		// not exist
		subInterfaces.add(subInterface);

	}

	public PhysicalInterface getPhysicalInterfaceParent() {
		return physicalInterfaceParent;
	}

	public void setPhysicalInterfaceParent(PhysicalInterface value) {
		this.physicalInterfaceParent = value;
	}

}
