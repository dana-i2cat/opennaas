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

/**
 * This Class contains accessor and mutator methods for all properties defined in the GRETunnelServiceConfiguration class as well as methods
 * comparable to the invokeMethods defined for this class. This Class implements the GRETunnelServiceConfigurationBean Interface. The
 * GRETunnelConfiguration class is described as follows:
 * 
 * GRETunnelServiceConfiguration connects an GRE Tunnel service to its configurations. The configurations are defined for the GRE Tunnel Service, and
 * so do not make sense as stand alone objects.
 * 
 * @author adrian
 */
public class GRETunnelServiceConfiguration extends Dependency implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8120481814591790508L;

	/**
	 * This constructor creates a GRETunnelServiceConfigurationBeanImpl Class which implements the GRETunnelServiceConfigurationBean Interface, and
	 * encapsulates the CIM class OSPFServiceConfiguration in a Java Bean. The OSPFServiceConfiguration class is described as follows:
	 * 
	 * GRETunnelServiceConfiguration connects an GRE Tunnel service to its configurations. The configurations are defined for the GRE Tunnel Service,
	 * and so do not make sense as stand alone objects.
	 * 
	 */
	public GRETunnelServiceConfiguration() {
	};

	/**
	 * This method creates an Association of the type GRETunnelServiceConfiguration between one GRETunnelService object and GRETunnelConfiguration
	 * object
	 */
	public static GRETunnelServiceConfiguration link(GRETunnelService
			antecedent, GRETunnelConfiguration dependent) {

		return (GRETunnelServiceConfiguration) Association.link(GRETunnelServiceConfiguration.class, antecedent, dependent);
	}// link

}