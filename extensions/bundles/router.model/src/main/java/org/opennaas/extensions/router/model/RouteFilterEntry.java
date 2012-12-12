package org.opennaas.extensions.router.model;

public class RouteFilterEntry extends FilterEntryBase {

	private String	address;

	private String	matchOption;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMatchOption() {
		return matchOption;
	}

	public void setMatchOption(String matchOption) {
		this.matchOption = matchOption;
	}
}
