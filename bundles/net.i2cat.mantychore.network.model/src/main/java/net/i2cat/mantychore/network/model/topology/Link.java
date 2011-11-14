package net.i2cat.mantychore.network.model.topology;

import java.util.List;

import net.i2cat.mantychore.network.model.predicates.LinkTo;

/**
 * A (collection of) network element(s) that can be represented as a link connection (ITU-T G.805 terminology) or as an edge on a vertex (in Graph theory). 
 * Typically a single (non-concatenated) link on a certain network layer (not necessarily the physical layer). 
 * A Link can be unidirectional or bidirectional and is a special case of a Broadcast Segment.
 * 
 * @author isart
 *
 */
public class Link extends BroadcastSegment {
	
	List<LinkTo> linkTo;

	public List<LinkTo> getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(List<LinkTo> linkTo) {
		this.linkTo = linkTo;
	}
}
