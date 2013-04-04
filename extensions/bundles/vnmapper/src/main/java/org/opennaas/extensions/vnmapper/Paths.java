package org.opennaas.extensions.vnmapper;

import java.util.ArrayList;

public class Paths {

	// / the class is used to store a list of paths between two nodes

	private int				node1Id;
	private int				node2Id;
	private ArrayList<Path>	paths	= new ArrayList<Path>();

	public int getNode1Id() {
		return node1Id;
	}

	public void setNode1Id(int node1Id) {
		this.node1Id = node1Id;
	}

	public int getNode2Id() {
		return node2Id;
	}

	public void setNode2Id(int node2Id) {
		this.node2Id = node2Id;
	}

	public ArrayList<Path> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}

}
