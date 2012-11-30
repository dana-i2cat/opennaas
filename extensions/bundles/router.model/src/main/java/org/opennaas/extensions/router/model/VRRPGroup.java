package org.opennaas.extensions.router.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Julio Carlos Barrera
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VRRPGroup extends Service implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7958971959845082335L;

	private int					vrrpName;
	private String				virtualIPAddress;

	public VRRPGroup() {
	}

	public int getVrrpName() {
		return vrrpName;
	}

	public void setVrrpName(int vrrpName) {
		this.vrrpName = vrrpName;
	}

	public String getVirtualIPAddress() {
		return virtualIPAddress;
	}

	public void setVirtualIPAddress(String virtualIPAddress) {
		this.virtualIPAddress = virtualIPAddress;
	}
}
