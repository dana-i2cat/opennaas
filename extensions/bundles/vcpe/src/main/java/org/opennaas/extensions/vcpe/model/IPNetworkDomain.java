package org.opennaas.extensions.vcpe.model;

import java.util.List;

public class IPNetworkDomain extends Domain {

	protected String		aSNumber;
	protected List<String>	iPAddressRanges;

	public String getASNumber() {
		return aSNumber;
	}

	public void setASNumber(String aSNumber) {
		this.aSNumber = aSNumber;
	}

	public List<String> getIPAddressRanges() {
		return iPAddressRanges;
	}

	public void setIPAddressRange(List<String> iPAddressRanges) {
		this.iPAddressRanges = iPAddressRanges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((aSNumber == null) ? 0 : aSNumber.hashCode());
		result = prime * result + ((iPAddressRanges == null) ? 0 : iPAddressRanges.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IPNetworkDomain other = (IPNetworkDomain) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;

		if (aSNumber == null) {
			if (other.aSNumber != null)
				return false;
		} else if (!aSNumber.equals(other.aSNumber))
			return false;
		if (iPAddressRanges == null) {
			if (other.iPAddressRanges != null)
				return false;
		} else if (!iPAddressRanges.equals(other.iPAddressRanges))
			return false;

		return true;
	}
}
