package org.opennaas.extensions.router.model;

import java.io.Serializable;

public class BGPSessionConfig extends LogicalElement implements Serializable {

	public BGPSessionConfig() {
	};

	/**
	 * The following constants are defined for use with the ValueMap/Values qualified property peerName.
	 */
	private String	peerName;

	/**
	 * This method returns the BGPPeerFiltering.peerName property value. This property is described as follows:
	 * 
	 * PeerName indicates the name of the neighbor peer this instance applies to.
	 * 
	 * @return String current peerName property value
	 * @exception Exception
	 */
	public String getPeerName() {

		return this.peerName;
	} // getPeerName

	/**
	 * This method sets the BGPPeerFiltering.peerName property value. This property is described as follows:
	 * 
	 * PeerName indicates the name of the neighbor peer this instance applies to.
	 * 
	 * @param String
	 *            new peerName property value
	 * @exception Exception
	 */
	public void setPeerName(String peerName) {

		this.peerName = peerName;
	} // setPeerName

}
