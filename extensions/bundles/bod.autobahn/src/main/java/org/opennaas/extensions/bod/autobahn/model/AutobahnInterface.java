package org.opennaas.extensions.bod.autobahn.model;

import net.geant.autobahn.useraccesspoint.PortType;

import org.opennaas.extensions.network.model.topology.Interface;

public class AutobahnInterface extends Interface
{
	private PortType portType;
	private boolean isLocal;

	public void setPortType(PortType portType)
	{
		this.portType = portType;
	}

	public PortType getPortType()
	{
		return portType;
	}

	public void setLocal(boolean isLocal)
	{
		this.isLocal = isLocal;
	}

	public boolean isLocal()
	{
		return isLocal;
	}

	@Override
	public String toString()
	{
		return (portType == null)
			? super.toString()
			: ((portType.getDescription() == null)
			   ? portType.getAddress()
			   : portType.getDescription()) + (isLocal ? ",local" : "");
	}
}
