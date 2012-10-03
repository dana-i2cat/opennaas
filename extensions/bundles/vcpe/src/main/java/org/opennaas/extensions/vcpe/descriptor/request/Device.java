package org.opennaas.extensions.vcpe.descriptor.request;

import java.util.List;

public class Device extends RequestElement {

	private String			name;

	private String			parentName;

	private List<Interface>	interfaces;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public List<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}

}
