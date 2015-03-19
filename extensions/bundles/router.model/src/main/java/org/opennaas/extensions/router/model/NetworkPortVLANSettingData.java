package org.opennaas.extensions.router.model;

/*
 * #%L
 * OpenNaaS :: CIM Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class NetworkPortVLANSettingData extends SettingData implements Serializable {

	public static final int		NATIVE_VLAN_DEFAULT_VALUE	= -1;

	private static final long	serialVersionUID		= -9054007445043019035L;

	private int					nativeVlanId;
	private String				portMode;
	
	private String				inputFilterName;
	private String				outputFilterName;
	private String				vlanMembers;

	public NetworkPortVLANSettingData() {
		// FIXME if the NetworkPortVLANSettingData is created and the setNativeVlanId is never called, there's no way to distinguish
		// betweeen the default value of the int (0) and the vlanId 0.
		nativeVlanId = NATIVE_VLAN_DEFAULT_VALUE;
	}

	public int getNativeVlanId() {
		return nativeVlanId;
	}

	public void setNativeVlanId(int nativeId) {
		this.nativeVlanId = nativeId;
	}

	public String getPortMode() {
		return portMode;
	}

	public void setPortMode(String portMode) {
		this.portMode = portMode;
	}

	public String getInputFilterName() {
		return inputFilterName;
	}

	public void setInputFilterName(String inputFilterName) {
		this.inputFilterName = inputFilterName;
	}

	public String getOutputFilterName() {
		return outputFilterName;
	}

	public void setOutputFilterName(String outputFilterName) {
		this.outputFilterName = outputFilterName;
	}

	public String getVlanMembers() {
		return vlanMembers;
	}

	public void setVlanMembers(String vlanMembers) {
		this.vlanMembers = vlanMembers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inputFilterName == null) ? 0 : inputFilterName.hashCode());
		result = prime * result + nativeVlanId;
		result = prime
				* result
				+ ((outputFilterName == null) ? 0 : outputFilterName.hashCode());
		result = prime * result
				+ ((portMode == null) ? 0 : portMode.hashCode());
		result = prime * result
				+ ((vlanMembers == null) ? 0 : vlanMembers.hashCode());
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
		NetworkPortVLANSettingData other = (NetworkPortVLANSettingData) obj;
		if (inputFilterName == null) {
			if (other.inputFilterName != null)
				return false;
		} else if (!inputFilterName.equals(other.inputFilterName))
			return false;
		if (nativeVlanId != other.nativeVlanId)
			return false;
		if (outputFilterName == null) {
			if (other.outputFilterName != null)
				return false;
		} else if (!outputFilterName.equals(other.outputFilterName))
			return false;
		if (portMode == null) {
			if (other.portMode != null)
				return false;
		} else if (!portMode.equals(other.portMode))
			return false;
		if (vlanMembers == null) {
			if (other.vlanMembers != null)
				return false;
		} else if (!vlanMembers.equals(other.vlanMembers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NetworkPortVLANSettingData [nativeVlanId=" + nativeVlanId
				+ ", portMode=" + portMode + ", inputFilterName="
				+ inputFilterName + ", outputFilterName=" + outputFilterName
				+ ", vlanMembers=" + vlanMembers + "]";
	}
	
	
}
