package org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;

@XmlRootElement
public class CreateVLANConfigurationRequest {

	private VLANConfiguration	vlanConfiguration;

	/**
	 * @return the vlanConfiguration
	 */
	public VLANConfiguration getVlanConfiguration() {
		return vlanConfiguration;
	}

	/**
	 * @param vlanConfiguration
	 *            the vlanConfiguration to set
	 */
	public void setVlanConfiguration(VLANConfiguration vlanConfiguration) {
		this.vlanConfiguration = vlanConfiguration;
	}

}
