package org.opennaas.extensions.genericnetwork.model.circuit;

/**
 * Wrapper class storing device and port IDs.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class DevicePortId {

	private String	deviceId;
	private String	devicePortId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDevicePortId() {
		return devicePortId;
	}

	public void setDevicePortId(String devicePortId) {
		this.devicePortId = devicePortId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((devicePortId == null) ? 0 : devicePortId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DevicePortId other = (DevicePortId) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (devicePortId == null) {
			if (other.devicePortId != null)
				return false;
		} else if (!devicePortId.equals(other.devicePortId))
			return false;
		return true;
	}
}
