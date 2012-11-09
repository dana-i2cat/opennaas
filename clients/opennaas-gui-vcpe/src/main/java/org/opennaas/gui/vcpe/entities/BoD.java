/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import javax.validation.Valid;

/**
 * @author Jordi
 * 
 */
public class BoD {

	@Valid
	private Interface	ifaceClient;
	@Valid
	private Interface	ifaceClientBackup;
	private Link		linkMaster;
	private Link		linkInter;
	private Link		linkBackup;

	/**
	 * @return the ifaceClient
	 */
	public Interface getIfaceClient() {
		return ifaceClient;
	}

	/**
	 * @param ifaceClient
	 *            the ifaceClient to set
	 */
	public void setIfaceClient(Interface ifaceClient) {
		this.ifaceClient = ifaceClient;
	}

	/**
	 * @return the ifaceClientBackup
	 */
	public Interface getIfaceClientBackup() {
		return ifaceClientBackup;
	}

	/**
	 * @param ifaceClientBackup
	 *            the ifaceClientBackup to set
	 */
	public void setIfaceClientBackup(Interface ifaceClientBackup) {
		this.ifaceClientBackup = ifaceClientBackup;
	}

	/**
	 * @return the linkMaster
	 */
	public Link getLinkMaster() {
		return linkMaster;
	}

	/**
	 * @param linkMaster
	 *            the linkMaster to set
	 */
	public void setLinkMaster(Link linkMaster) {
		this.linkMaster = linkMaster;
	}

	/**
	 * @return the linkInter
	 */
	public Link getLinkInter() {
		return linkInter;
	}

	/**
	 * @param linkInter
	 *            the linkInter to set
	 */
	public void setLinkInter(Link linkInter) {
		this.linkInter = linkInter;
	}

	/**
	 * @return the linkBackup
	 */
	public Link getLinkBackup() {
		return linkBackup;
	}

	/**
	 * @param linkBackup
	 *            the linkBackup to set
	 */
	public void setLinkBackup(Link linkBackup) {
		this.linkBackup = linkBackup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BoD [ifaceClient=" + ifaceClient + ", ifaceClientBackup=" + ifaceClientBackup + ", linkMaster=" + linkMaster + ", linkInter=" + linkInter + ", linkBackup=" + linkBackup + "]";
	}

}
