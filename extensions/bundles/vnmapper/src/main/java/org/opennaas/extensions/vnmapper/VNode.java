package org.opennaas.extensions.vnmapper;

public class VNode {

	private int		id;
	private int		capacity;
	private String	pnodeID;

	public VNode() {
		pnodeID = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getPnodeID() {
		return pnodeID;
	}

	public void setPnodeID(String pnodeID) {
		this.pnodeID = pnodeID;
	}

}
