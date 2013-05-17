package org.opennaas.extensions.vcpe.model;

import java.util.List;

public class IPNetworkDomain extends Domain {

	protected String		owner;
	protected String		aSNumber;
	protected List<String>	iPAddressRanges;

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the aSNumber
	 */
	public String getASNumber() {
		return aSNumber;
	}

	/**
	 * @param aSNumber
	 *            the aSNumber to set
	 */
	public void setASNumber(String aSNumber) {
		this.aSNumber = aSNumber;
	}

	/**
	 * @return the iPAddressRanges
	 */
	public List<String> getIPAddressRanges() {
		return iPAddressRanges;
	}

	/**
	 * @param iPAddressRanges
	 *            the iPAddressRanges to set
	 */
	public void setIPAddressRanges(List<String> iPAddressRanges) {
		this.iPAddressRanges = iPAddressRanges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((aSNumber == null) ? 0 : aSNumber.hashCode());
		result = prime * result + ((iPAddressRanges == null) ? 0 : iPAddressRanges.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IPNetworkDomain other = (IPNetworkDomain) obj;
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
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

}
