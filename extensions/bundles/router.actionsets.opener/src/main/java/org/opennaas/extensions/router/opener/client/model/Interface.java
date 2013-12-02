package org.opennaas.extensions.router.opener.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://www.craax.upc.edu/axis2/quagga_openapi")
@XmlAccessorType(XmlAccessType.FIELD)
public class Interface {

	@XmlElement(name = "name")
	private String	name;

	@XmlElement(name = "familytype")
	private String	familyType;

	@XmlElement(name = "ip")
	private IPData	ip;

	@XmlElement(name = "ip6")
	private IPData	ip6;

	@XmlElement(name = "hardware-address")
	private String	hardwareAddress;

	@XmlElement(name = "status")
	private String	status;

	@XmlElement(name = "mtu")
	private String	mtu;

	@XmlElement(name = "metric")
	private String	metric;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFamilyType() {
		return familyType;
	}

	public void setFamilyType(String familyType) {
		this.familyType = familyType;
	}

	public IPData getIp() {
		return ip;
	}

	public void setIp(IPData ip) {
		this.ip = ip;
	}

	public IPData getIp6() {
		return ip6;
	}

	public void setIp6(IPData ip6) {
		this.ip6 = ip6;
	}

	public String getHardwareAddress() {
		return hardwareAddress;
	}

	public void setHardwareAddress(String hardwareAddress) {
		this.hardwareAddress = hardwareAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMtu() {
		return mtu;
	}

	public void setMtu(String mtu) {
		this.mtu = mtu;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

}
