package org.opennaas.extensions.router.model;

import java.io.Serializable;

/**
 * This association describes the owning relationship between BGPPeerGroup (owner) and BGPPeerFiltering (owned). Using this association, an instance
 * of BGPPeerGroup may be linked to many BGPPeerFiltering instances. Using this association, an instance of BGPPeerFiltering may be linked to a single
 * BGPPeerGroup instance, but must not be linked to more than one.
 */
public class BGPSessionConfigInBGPPeerGroup extends Dependency implements Serializable {

	public BGPSessionConfigInBGPPeerGroup() {
	};

	/**
	 * This method create an Association of the type BGPFilteringInBGPPeerGroup between one BGPPeerGroup object and BGPPeerFiltering object
	 */
	public static BGPSessionConfigInBGPPeerGroup link(BGPPeerGroup antecedent, BGPSessionConfig
			dependent) {

		return (BGPSessionConfigInBGPPeerGroup) Association.link(BGPSessionConfigInBGPPeerGroup.class, antecedent, dependent);
	}// link

} // Class BGPFilteringInBGPPeerGroup

