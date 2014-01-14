package org.opennaas.extensions.vcpe.model.routing;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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
