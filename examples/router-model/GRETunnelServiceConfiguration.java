package org.opennaas.extensions.router.model;

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