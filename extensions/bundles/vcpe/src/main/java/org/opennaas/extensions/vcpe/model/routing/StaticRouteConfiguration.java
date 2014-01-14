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
