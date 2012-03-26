package org.opennaas.extensions.network.model.topology;

import java.util.List;

/**
 * A (collection of) network element(s) that can be represented as a link connection (ITU-T G.805 terminology) or as an edge on a vertex (in Graph theory).
 * Typically a single (non-concatenated) link on a certain network layer (not necessarily the physical layer).
 * A Link can be unidirectional or bidirectional and is a special case of a Broadcast Segment.
 *
 * @author isart
 *
 */
public class Link extends BroadcastSegment {

	Interface source;
	Interface sink;

	boolean isBidirectional = false;


	public Interface getSource() {
		return source;
	}

	public void setSource(Interface source) {
		this.source = source;
	}

	public Interface getSink() {
		return sink;
	}

	public void setSink(Interface sink) {
		this.sink = sink;
	}

	public boolean isBidirectional() {
		return isBidirectional;
	}

	public void setBidirectional(boolean isBidirectional) {
		this.isBidirectional = isBidirectional;
	}

}
