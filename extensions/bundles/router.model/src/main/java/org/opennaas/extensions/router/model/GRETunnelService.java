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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This Class contains accessor and mutator methods for all properties of the IPConfigurationService class as well as methods comparable to the
 * invokeMethods defined for this class. This Class implements the GRETunnelServiceBean Interface.
 * 
 * @author adrian
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GRETunnelService extends Service implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3996193388905927248L;

	public GRETunnelService() {
	};

	/**
	 * Adds a new GRETunnelServiceConfiguration association between a given GRETunnelConfiguration and this element
	 * 
	 * @param greTunnelConfiguration
	 */
	public void setGRETunnelConfiguration(GRETunnelConfiguration greTunnelConfiguration) {
		if (greTunnelConfiguration != null)
			GRETunnelServiceConfiguration.link(this, greTunnelConfiguration);
	}

	/**
	 * Removes the GRETunnelServiceConfiguration association between the given GRETunnelConfiguration and this element.
	 * 
	 * @param greTunnelConfiguration
	 */
	public void removeGRETunnelConfiguration(GRETunnelConfiguration greTunnelConfiguration) {
		if (greTunnelConfiguration != null) {
			Association a = this.getFirstToAssociationByTypeAndElement(GRETunnelServiceConfiguration.class, greTunnelConfiguration);
			if (a != null)
				a.unlink();
		}
	}

	/**
	 * Returns the list of all GRETunnelConfiguration associated to this element.
	 * 
	 * @return
	 */
	public GRETunnelConfiguration getGRETunnelConfiguration() {
		return (GRETunnelConfiguration) this.getFirstToAssociatedElementByType(GRETunnelServiceConfiguration.class);
	}
}
