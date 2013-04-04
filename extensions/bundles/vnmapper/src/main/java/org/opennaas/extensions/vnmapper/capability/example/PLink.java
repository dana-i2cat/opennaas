package org.opennaas.extensions.vnmapper.capability.example;

import java.io.Serializable;

public class PLink implements Serializable
{
	public int	id;
	public int	node1Id;
	public int	node2Id;
	public int	capacity;
	// public int availableCapacity;
	public int	delay;

	public PLink()
	{
		id = -1;
	}

}
