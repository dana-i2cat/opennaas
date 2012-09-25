package org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;

@XmlRootElement
public class AddStaticVLANRequest {

	private StaticVLANRegistrationEntry	vlanRegistration;

	/**
	 * @return the vlanRegistration
	 */
	public StaticVLANRegistrationEntry getVlanRegistration() {
		return vlanRegistration;
	}

	/**
	 * @param vlanRegistration
	 *            the vlanRegistration to set
	 */
	public void setVlanRegistration(StaticVLANRegistrationEntry vlanRegistration) {
		this.vlanRegistration = vlanRegistration;
	}

}
