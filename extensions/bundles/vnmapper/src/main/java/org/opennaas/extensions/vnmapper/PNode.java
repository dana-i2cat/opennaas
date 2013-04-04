package org.opennaas.extensions.vnmapper;

import java.io.Serializable;

public class PNode implements Serializable
{

	private static final long	serialVersionUID	= 7689643598919211921L;

	private int					id;
	private int					capacity;
	private String				pnodeID;
	private int					pathNum;

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

	public int getPathNum() {
		return pathNum;
	}

	public void setPathNum(int pathNum) {
		this.pathNum = pathNum;
	}

	public void increaseCapacity(int value) {
		capacity += value;
	}

	public void decreaseCapacity(int value) {
		capacity -= value;
	}

}
