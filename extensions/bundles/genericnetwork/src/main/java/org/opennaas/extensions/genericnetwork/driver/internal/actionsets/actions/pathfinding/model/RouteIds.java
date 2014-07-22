package org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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
