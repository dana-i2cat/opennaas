package org.opennaas.extensions.router.model;

import java.util.List;

public class IPAddressPrefixList extends LogicalElement {

	private List<String>	prefixes;

	public List<String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(List<String> prefixes) {
		this.prefixes = prefixes;
	}

}
