package org.opennaas.extensions.vcpe.model.routing;

public class StaticRouteConfiguration {

	private String	destination	= null;
	private String	nextHop		= null;
	private boolean	discard		= false;

	public StaticRouteConfiguration() {
	}

	public StaticRouteConfiguration(String destination, String nextHop, boolean isDiscard) {
		this.destination = destination;
		this.nextHop = nextHop;
		this.discard = isDiscard;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getNextHop() {
		return nextHop;
	}

	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

	public boolean isDiscard() {
		return discard;
	}

	public void setDiscard(boolean discard) {
		this.discard = discard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + (discard ? 1231 : 1237);
		result = prime * result + ((nextHop == null) ? 0 : nextHop.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaticRouteConfiguration other = (StaticRouteConfiguration) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (discard != other.discard)
			return false;
		if (nextHop == null) {
			if (other.nextHop != null)
				return false;
		} else if (!nextHop.equals(other.nextHop))
			return false;
		return true;
	}
}
