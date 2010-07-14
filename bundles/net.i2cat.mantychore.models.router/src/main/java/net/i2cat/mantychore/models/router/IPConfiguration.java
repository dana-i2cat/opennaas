package net.i2cat.mantychore.models.router;

import java.util.ArrayList;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 * Contain parameters for the configuration of an network interface.
 */
public class IPConfiguration {
	/**
	 * Subnetwork address obtain from the ipaddress and the mask.
	 */
	@NotBlank
	protected String		ipSubNetwork;
	/**
	 * Add the funcionability of fastSwitching to the interface. NOT IMPLEMENTED
	 */
	protected String		fastSwitching;
	/**
	 * NOT IMPLEMENTED
	 */
	protected String		nat;
	/**
	 * NOT IMPLEMENTED
	 */
	protected List<String>	rule;
	protected String		ipv4Address;
	protected String		ipv4Mask;
	/**
	 * NOT USED
	 */
	protected String		ipv4Broadcast;
	protected String		ipv6Address;
	protected String		ipv6Prefix;
	/**
	 * NOT USED
	 */
	protected String		ipv6Broadcast;
	/**
	 * Representation of the IP protocol version. NOT USED
	 */
	protected String		ipVersion;

	public String getIpSubNetwork() {
		return ipSubNetwork;
	}

	public void setIpSubNetwork(String value) {
		this.ipSubNetwork = value;
	}

	public String getFastSwitching() {
		return fastSwitching;
	}

	public void setFastSwitching(String value) {
		this.fastSwitching = value;
	}

	public String getNat() {
		return nat;
	}

	public void setNat(String value) {
		this.nat = value;
	}

	public List<String> getRule() {
		if (rule == null) {
			rule = new ArrayList<String>();
		}
		return this.rule;
	}

	public String getIpv4Address() {
		return ipv4Address;
	}

	public void setIpv4Address(String value) {
		this.ipv4Address = value;
	}

	public String getIpv4Mask() {
		return ipv4Mask;
	}

	public void setIpv4Mask(String value) {
		this.ipv4Mask = value;
	}

	public String getIpv4Broadcast() {
		return ipv4Mask;
	}

	public void setIpv4Broadcast(String value) {
		this.ipv4Mask = value;
	}

	public String getIpv6Address() {
		return ipv6Address;
	}

	public void setIpv6Address(String value) {
		this.ipv6Address = value;
	}

	public String getIpv6Prefix() {
		return ipv6Prefix;
	}

	public void setIpv6Prefix(String value) {
		this.ipv6Prefix = value;
	}

	public String getIpv6Broadcast() {
		return ipv4Mask;
	}

	public void setIpv6Broadcast(String value) {
		this.ipv4Mask = value;
	}

	public String getIpVersion() {
		return ipVersion;
	}

	public void setIpVersion(String value) {
		this.ipVersion = value;
	}

}
