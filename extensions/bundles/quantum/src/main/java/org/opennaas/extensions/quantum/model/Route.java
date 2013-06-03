package org.opennaas.extensions.quantum.model;

/**
 * mixin of a route
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Route {

	private String	destination;
	private String	nexthop;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getNexthop() {
		return nexthop;
	}

	public void setNexthop(String nexthop) {
		this.nexthop = nexthop;
	}
}
