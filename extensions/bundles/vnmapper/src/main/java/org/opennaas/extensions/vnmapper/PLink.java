package org.opennaas.extensions.vnmapper;

import java.io.Serializable;

public class PLink implements Serializable
{
	private int	id;
	private int	node1Id;
	private int	node2Id;
	private int	capacity;
	// public int availableCapacity;
	private int	delay;

	public PLink()
	{
		id = -1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void increaseCapacity(int value) {
		capacity += value;
	}

	public void decreaseCapacity(int value) {
		capacity -= value;
	}
}
