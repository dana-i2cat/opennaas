package org.opennaas.extensions.xifi.capability.e2e.model;

/*
 * #%L
 * OpenNaaS :: XIFI
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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

import java.util.HashSet;
import java.util.Set;

/**
 * XIFI Configuration
 * 
 * @author Julio Carlos Barrera
 *
 */
public class XIFIConfiguration {

	private String		abnoEndpoint;
	private Set<Region>	regions	= new HashSet<Region>();

	public String getAbnoEndpoint() {
		return abnoEndpoint;
	}

	public void setABNOEndpoint(String abnoEndpoint) {
		this.abnoEndpoint = abnoEndpoint;
	}

	public Set<Region> getRegions() {
		return regions;
	}

	public void setRegions(Set<Region> regions) {
		this.regions = regions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abnoEndpoint == null) ? 0 : abnoEndpoint.hashCode());
		result = prime * result + ((regions == null) ? 0 : regions.hashCode());
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
		XIFIConfiguration other = (XIFIConfiguration) obj;
		if (abnoEndpoint == null) {
			if (other.abnoEndpoint != null)
				return false;
		} else if (!abnoEndpoint.equals(other.abnoEndpoint))
			return false;
		if (regions == null) {
			if (other.regions != null)
				return false;
		} else if (!regions.equals(other.regions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XIFIConfiguration [abnoEndpoint=" + abnoEndpoint + ", regions=" + regions + "]";
	}

}
