package org.opennaas.extensions.router.model;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement
public class AggregatedOptions implements Serializable {

	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long	serialVersionUID	= -8960039625776709380L;

	private String				minimumLinks		= "";
	private String				linkSpeed			= "";
	private boolean				lacpActive			= false;

	/**
	 * @return the minimumLinks
	 */
	public String getMinimumLinks() {
		return minimumLinks;
	}

	/**
	 * @param minimumLinks
	 *            the minimumLinks to set
	 */
	public void setMinimumLinks(String minimumLinks) {
		this.minimumLinks = minimumLinks;
	}

	/**
	 * @return the linkSpeed
	 */
	public String getLinkSpeed() {
		return linkSpeed;
	}

	/**
	 * @param linkSpeed
	 *            the linkSpeed to set
	 */
	public void setLinkSpeed(String linkSpeed) {
		this.linkSpeed = linkSpeed;
	}

	/**
	 * @return the lacpActive
	 */
	public boolean isLacpActive() {
		return lacpActive;
	}

	/**
	 * @param lacpActive
	 *            the lacpActive to set
	 */
	public void setLacpActive(boolean lacpActive) {
		this.lacpActive = lacpActive;
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
		result = prime * result + (lacpActive ? 1231 : 1237);
		result = prime * result + ((linkSpeed == null) ? 0 : linkSpeed.hashCode());
		result = prime * result + ((minimumLinks == null) ? 0 : minimumLinks.hashCode());
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
		AggregatedOptions other = (AggregatedOptions) obj;
		if (lacpActive != other.lacpActive)
			return false;
		if (linkSpeed == null) {
			if (other.linkSpeed != null)
				return false;
		} else if (!linkSpeed.equals(other.linkSpeed))
			return false;
		if (minimumLinks == null) {
			if (other.minimumLinks != null)
				return false;
		} else if (!minimumLinks.equals(other.minimumLinks))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AggregatedOptions [minimumLinks=" + minimumLinks + ", linkSpeed=" + linkSpeed + ", lacpActive=" + lacpActive + "]";
	}
}
