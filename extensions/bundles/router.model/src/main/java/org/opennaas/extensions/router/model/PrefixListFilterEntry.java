package org.opennaas.extensions.router.model;


public class PrefixListFilterEntry extends FilterEntryBase {

	private IPAddressPrefixList	prefixList;

	public enum Action {
		PERMIT,
		DENY
	}

	private Action	action;

	/**
	 * This defines whether prefixes matching prefixList should we permitted (white list filter) or denied (blacklist filter).
	 * 
	 * @return int current action property value
	 * @exception Exception
	 */
	public Action getAction() {

		return this.action;
	} // getAction

	/**
	 * This method sets the PrefixListFilterEntry.action property value. This property is described as follows:
	 * 
	 * This defines whether prefixes matching prefixList should we permitted (white list filter) or denied (blacklist filter).
	 * 
	 * @param int new action property value
	 * @exception Exception
	 */
	public void setAction(Action action) {

		this.action = action;
	} // setAction

	public IPAddressPrefixList getPrefixList() {
		return prefixList;
	}

	public void setPrefixList(IPAddressPrefixList prefixList) {
		this.prefixList = prefixList;
	}

}
