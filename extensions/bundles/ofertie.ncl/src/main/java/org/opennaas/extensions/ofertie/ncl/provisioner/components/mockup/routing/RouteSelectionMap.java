package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteSelectionMap {

	/**
	 * Key: RouteSelectionInput, Value: RouteIds
	 */
	private Map<RouteSelectionInput, RouteIds>	routeMapping;

	public RouteSelectionMap() {
		routeMapping = new HashMap<RouteSelectionInput, RouteIds>();
	}

	/**
	 * @return the routeMapping
	 */
	public Map<RouteSelectionInput, RouteIds> getRouteMapping() {
		return routeMapping;
	}

	/**
	 * @param routeMapping
	 *            the routeMapping to set
	 */
	public void setRouteMapping(Map<RouteSelectionInput, RouteIds> routeMapping) {
		this.routeMapping = routeMapping;
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
		result = prime * result + ((routeMapping == null) ? 0 : routeMapping.hashCode());
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
		RouteSelectionMap other = (RouteSelectionMap) obj;
		if (routeMapping == null) {
			if (other.routeMapping != null)
				return false;
		} else if (!routeMapping.equals(other.routeMapping))
			return false;
		return true;
	}

}
