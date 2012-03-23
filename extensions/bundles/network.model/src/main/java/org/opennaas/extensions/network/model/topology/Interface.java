package org.opennaas.extensions.network.model.topology;


/**
 * A configured interface. Thus an entity that can transport data at a given layer.
 *
 * @author isart
 *
 */
public class Interface extends ConnectionPoint {

	Device			device;

	Link			linkTo;
	CrossConnect	switchedTo;

	/**
	 * end-to-end path
	 */
	Path			connectedTo;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Path getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(Path connectedTo) {
		this.connectedTo = connectedTo;
	}

	public Link getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(Link linkTo) {
		this.linkTo = linkTo;
	}

	public CrossConnect getSwitchedTo() {
		return switchedTo;
	}

	public void setSwitchedTo(CrossConnect switchedTo) {
		this.switchedTo = switchedTo;
	}
}
