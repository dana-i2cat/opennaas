package org.opennaas.core.platformmanager;

import javax.xml.bind.annotation.XmlAttribute;

public class NetInf {

	private String name = null;
	private String displayName = null;
	private String ipAddress = null;
	private String macAddress = null;
	private String mtu = null;
	private String hostname = null;
	private boolean isLoopback = false;
	private boolean isPointToPoint = false;
	private boolean isUp = false;
	private boolean supportsMulticast = false;
	private boolean virtual = false;

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "displayName")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@XmlAttribute(name = "ipAddress")
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@XmlAttribute(name = "macAddress")
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@XmlAttribute(name = "hostname")
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@XmlAttribute(name = "mtu")
	public String getMtu() {
		return mtu;
	}

	public void setMtu(String mtu) {
		this.mtu = mtu;
	}

	@XmlAttribute(name = "loopback")
	public boolean isLoopback() {
		return isLoopback;
	}

	public void setLoopback(boolean isLoopback) {
		this.isLoopback = isLoopback;
	}

	@XmlAttribute(name = "pointToPoint")
	public boolean isPointToPoint() {
		return isPointToPoint;
	}

	public void setPointToPoint(boolean isPointToPoint) {
		this.isPointToPoint = isPointToPoint;
	}

	@XmlAttribute(name = "up")
	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

	@XmlAttribute(name = "supportsMulticast")
	public boolean isSupportsMulticast() {
		return supportsMulticast;
	}

	public void setSupportsMulticast(boolean supportsMulticast) {
		this.supportsMulticast = supportsMulticast;
	}

	@XmlAttribute(name = "virtual")
	public boolean isVirtual() {
		return virtual;
	}

	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}

	public String toString(){
		String result = "      Network Interface  \n";
		result = result + "         Name: " + getName() + "\n";
		result = result + "         Display Name: " + getDisplayName() + "\n";
		result = result + "         IP Address: " + getIpAddress() + "\n";
		result = result + "         Hostname: " + getHostname() + "\n";
		result = result + "         MAC Address: " + getMacAddress() + "\n";
		result = result + "         MTU: " + getMtu() + "\n";
		result = result + "         Is up?: " + isUp() + "\n";
		result = result + "         Is Virtual?: " + isVirtual() + "\n";
		result = result + "         Is Point to Point?: " + isPointToPoint() + "\n";
		result = result + "         Is Loopback?: " + isLoopback() + "\n";
		result = result + "         Supports multicast?: " + isLoopback() + "\n";
		return result;
	}

}
