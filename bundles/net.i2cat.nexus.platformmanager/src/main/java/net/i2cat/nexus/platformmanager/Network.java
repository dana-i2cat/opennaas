package net.i2cat.nexus.platformmanager;

import javax.xml.bind.annotation.XmlAttribute;

public class Network {
	private String ipAddress = null;
	private String hostname = null;
	
	public Network(){
	}

	@XmlAttribute(name = "ipAddress")
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@XmlAttribute(name = "hostname")
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String toString(){
		String result = "   Network \n";
		result = result + "      IP Address: " + getIpAddress() + "\n";
		result = result + "      Hostname: " + getHostname() + "\n";
		return result;
	}
	
}
