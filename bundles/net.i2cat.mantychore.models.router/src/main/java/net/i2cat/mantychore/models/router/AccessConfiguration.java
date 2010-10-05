package net.i2cat.mantychore.models.router;


public class AccessConfiguration {

	protected String	ipAccessAddress;
	protected int		accessPort;
	protected String	transportType;
	protected String	protocolType;

	public String getIpAccessAddress() {
		return ipAccessAddress;
	}

	public void setIpAccessAddress(String value) {
		this.ipAccessAddress = value;
	}

	public int getAccessPort() {
		return accessPort;
	}

	public void setAccessPort(int value) {
		this.accessPort = value;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String value) {
		this.transportType = value;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String value) {
		this.protocolType = value;
	}

}
