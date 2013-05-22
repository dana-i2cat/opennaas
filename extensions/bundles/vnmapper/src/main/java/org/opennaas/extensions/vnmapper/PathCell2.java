package org.opennaas.extensions.vnmapper;

// / the next class is used to store information during the search for a path (LB)

public class PathCell2 {

	private int	nodeId;
	private int	linkNum;
	private int	delay;
	private int	prev;
	private int	remaining;	// remaining avaialble bandwidth
	private int	passed;

	public PathCell2() {
		this.remaining = -1;
		this.prev = -1;
		this.linkNum = 0;
		this.delay = 0;
		this.passed = 0;
	}

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

	public int getPrev() {
		return prev;
	}

	public void setPrev(int prev) {
		this.prev = prev;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public int getPassed() {
		return passed;
	}

	public void setPassed(int passed) {
		this.passed = passed;
	}

}
