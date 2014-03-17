package org.opennaas.extensions.router.capabilities.api.model.staticroute;

/*
 * #%L
 * OpenNaaS :: Router :: Capabilities :: API
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;
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
public class StaticRouteCollection implements Serializable {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= -6723247102166221152L;
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
