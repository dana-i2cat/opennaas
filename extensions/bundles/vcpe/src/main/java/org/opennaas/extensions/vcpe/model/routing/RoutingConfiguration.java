package org.opennaas.extensions.vcpe.model.routing;

import java.util.List;

public class RoutingConfiguration {

	private List<StaticRouteConfiguration>	staticRoutes;

	public List<StaticRouteConfiguration> getStaticRoutes() {
		return staticRoutes;
	}

	public void setStaticRoutes(List<StaticRouteConfiguration> staticRoutes) {
		this.staticRoutes = staticRoutes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((staticRoutes == null) ? 0 : staticRoutes.hashCode());
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
		RoutingConfiguration other = (RoutingConfiguration) obj;
		if (staticRoutes == null) {
			if (other.staticRoutes != null)
				return false;
		} else if (!staticRoutes.equals(other.staticRoutes))
			return false;
		return true;
	}
}
