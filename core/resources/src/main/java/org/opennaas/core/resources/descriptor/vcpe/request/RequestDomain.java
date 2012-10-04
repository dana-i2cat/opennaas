package org.opennaas.core.resources.descriptor.vcpe.request;

import java.util.List;

import javax.xml.bind.annotation.XmlIDREF;

public class RequestDomain extends RequestElement {

	@XmlIDREF
	private List<RequestDevice>		devices;

	@XmlIDREF
	private List<RequestInterface>	interfaces;

	public List<RequestDevice> getDevices() {
		return devices;
	}

	public void setDevices(List<RequestDevice> devices) {
		this.devices = devices;
	}

	public List<RequestInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<RequestInterface> interfaces) {
		this.interfaces = interfaces;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((devices == null) ? 0 : devices.hashCode());
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
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
		RequestDomain other = (RequestDomain) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (devices == null) {
			if (other.devices != null)
				return false;
		} else if (!devices.equals(other.devices))
			return false;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;
		return true;
	}

}
