package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing;

import java.util.ArrayList;
import java.util.List;

public class RouteIds {

	private List<String>	routeIds;

	public RouteIds() {
		routeIds = new ArrayList<String>();
	}

	public List<String> getRouteIds() {
		return routeIds;
	}

	public void setRouteIds(List<String> routeIds) {
		this.routeIds = routeIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((routeIds == null) ? 0 : routeIds.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteIds other = (RouteIds) obj;
		if (routeIds == null) {
			if (other.routeIds != null)
				return false;
		} else if (!routeIds.equals(other.routeIds))
			return false;
		return true;
	}

}
