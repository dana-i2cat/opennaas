package net.i2cat.mantychore.network.model.topology;

import java.util.List;

/**
 * A configured interface. Thus an entity that can transport data at a given layer. 
 * 
 * @author isart
 *
 */
public class Interface extends ConnectionPoint {
	
	
	Device device;
	
	Link isSourceOf;
	Link isSinkOf;
	
	List<CrossConnect> switchedTo;
	
	/**
	 *  end-to-end path
	 */
	Path connectedTo;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Link getIsSourceOf() {
		return isSourceOf;
	}

	public void setIsSourceOf(Link isSourceOf) {
		this.isSourceOf = isSourceOf;
	}

	public Link getIsSinkOf() {
		return isSinkOf;
	}

	public void setIsSinkOf(Link isSinkOf) {
		this.isSinkOf = isSinkOf;
	}

	public List<CrossConnect> getSwitchedTo() {
		return switchedTo;
	}

	public void setSwitchedTo(List<CrossConnect> switchedTo) {
		this.switchedTo = switchedTo;
	}

	public Path getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(Path connectedTo) {
		this.connectedTo = connectedTo;
	}
}
