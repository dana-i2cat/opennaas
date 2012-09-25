package org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeleteStaticVLANRequest {

	private int	vlanId;

	/**
	 * @return the vlanId
	 */
	public int getVlanId() {
		return vlanId;
	}

	/**
	 * @param vlanId
	 *            the vlanId to set
	 */
	public void setVlanId(int vlanId) {
		this.vlanId = vlanId;
	}

}
