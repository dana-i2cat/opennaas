package org.opennaas.extensions.router.capabilities.api.model.staticroute;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(name = "staticRoutes", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class StaticRouteCollection {

	@XmlElement(name = "staticRoute")
	private Collection<StaticRoute>	staticRoutes;

	public Collection<StaticRoute> getStaticRoutes() {
		return staticRoutes;
	}

	public void setStaticRoutes(Collection<StaticRoute> staticRoutes) {
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
		StaticRouteCollection other = (StaticRouteCollection) obj;
		if (staticRoutes == null) {
			if (other.staticRoutes != null)
				return false;
		} else if (!staticRoutes.equals(other.staticRoutes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StaticRouteList [staticRoutes=" + staticRoutes.toString() + "]";
	}

}
