package org.opennaas.extensions.vnmapper;

import java.util.ArrayList;

public class VNTNodeMappingCell {

	private int					vid;
	private ArrayList<Integer>	possibleRealNodes;
	private int					chosenRealNode;
	private int					connectionsNum;	// number of connection to the already mapped vnodes
	private int					pointer;

	public VNTNodeMappingCell() {
		possibleRealNodes = new ArrayList<Integer>();
	}

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public ArrayList<Integer> getPossibleRealNodes() {
		return possibleRealNodes;
	}

	public void setPossibleRealNodes(ArrayList<Integer> possibleRealNodes) {
		this.possibleRealNodes = possibleRealNodes;
	}

	public int getChosenRealNode() {
		return chosenRealNode;
	}

	public void setChosenRealNode(int chosenRealNode) {
		this.chosenRealNode = chosenRealNode;
	}

	public int getConnectionsNum() {
		return connectionsNum;
	}

	public void setConnectionsNum(int connectionsNum) {
		this.connectionsNum = connectionsNum;
	}

	public int getPointer() {
		return pointer;
	}

	public void setPointer(int pointer) {
		this.pointer = pointer;
	}

	public void incrementConnectionsNum() {
		this.connectionsNum++;
	}

	public void decrementConnectionsNum() {
		this.connectionsNum--;
	}
}
