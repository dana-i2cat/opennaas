package org.opennaas.extensions.vnmapper;

public class PathCell {
	// / the next class is used to store information during the search for a path (SPF)

	private int			nodeId;
	private int			linkNum;
	private int			delay;
	private PathCell	prev;

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getLinkNum() {
		return linkNum;
	}

	public void setLinkNum(int linkNum) {
		this.linkNum = linkNum;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public PathCell getPrev() {
		return prev;
	}

	public void setPrev(PathCell prev) {
		this.prev = prev;
	}

}
