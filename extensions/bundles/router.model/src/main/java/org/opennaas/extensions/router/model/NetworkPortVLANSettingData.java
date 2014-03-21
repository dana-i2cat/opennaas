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

	private static final long	serialVersionUID	= -9054007445043019035L;

	private String				nativeId;
	private String				porMode;

	public String getNativeId() {
		return nativeId;
	}

	public void setNativeId(String nativeId) {
		this.nativeId = nativeId;
	}

	public String getPorMode() {
		return porMode;
	}

	public void setPorMode(String porMode) {
		this.porMode = porMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nativeId == null) ? 0 : nativeId.hashCode());
		result = prime * result + ((porMode == null) ? 0 : porMode.hashCode());
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
		if (nativeId == null) {
			if (other.nativeId != null)
				return false;
		} else if (!nativeId.equals(other.nativeId))
			return false;
		if (porMode == null) {
			if (other.porMode != null)
				return false;
		} else if (!porMode.equals(other.porMode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NetworkPortVLANSettingData [nativeId=" + nativeId + ", porMode=" + porMode + "]";
	}

}
