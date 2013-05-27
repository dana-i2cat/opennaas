package org.opennaas.extensions.router.model;

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
