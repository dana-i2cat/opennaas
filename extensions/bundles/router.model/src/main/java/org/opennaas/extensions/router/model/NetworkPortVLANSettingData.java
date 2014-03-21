package org.opennaas.extensions.router.model;

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
