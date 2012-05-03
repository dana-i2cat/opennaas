package org.opennaas.extensions.router.model;

import java.io.Serializable;

/**
 * This class contains accessor and mutator methods for all properties of the GRETunnelConfiguration class as well as methods comparable to the
 * invokeMethods defined for this class. This Class implements the OSPFAreaConfigurationBean Interface.
 * 
 * Each router configuring a GRE Tunnel in one of its interfaces has to set the public source and destination addresses. That means, the public IP
 * address of the local device and the public IP address of the remote one.
 * 
 * @author adrian
 */
public class GRETunnelConfiguration extends LogicalElement implements
		Serializable {

	private String	destinationAddress;
	private String	sourceAddress;
	private int		key;

	/**
	 * This constructor creates a GRETunnelConfigurationBeanImpl Class which implements the GRETunnelConfigurationInterface, and encapsulates the
	 * GRETunnelConfiguration in a Java Bean. The GRETunnelConfiguration class is described as follows:
	 * 
	 * This class is deprecated following discussion as to the need of a modeling a GRETunnel as a service. GRE is a simple IP packet encapsulation
	 * protocol defined by RFC 2784. It's used to send packets between two remote networks, without parsing them as IP packets by other intervening
	 * devices. It's able to transport IPv6 traffic as well
	 */
	public GRETunnelConfiguration() {
	};

	/**
	 * This method returns the GRETunnelConfiguration.sourceAddress property value. This property is described as follows:
	 * 
	 * A string indicating the public IP address of the local device.
	 * 
	 * @return String current sourceAddress property value
	 */
	public String getSourceAddress() {
		return this.sourceAddress;
	}

	/**
	 * This method sets the GRETunnelConfiguration.sourceAddress property value. This property is described as follows:
	 * 
	 * A string indicating the public IP address of the local device.
	 * 
	 * @param String
	 *            new sourceAddress property value
	 */
	public void setSourceAddress(String address) {
		this.sourceAddress = address;
	}

	/**
	 * This method returns the GRETunnelConfiguration.destinationAddress property value. This property is described as follows:
	 * 
	 * A string indicating the public IP address of the remote device.
	 * 
	 * @return String current destinationeAddress property value
	 */
	public String getDestinationAddress() {
		return this.destinationAddress;

	}

	/**
	 * This method sets the GRETunnelConfiguration.destinationAddress property value. This property is described as follows:
	 * 
	 * A string indicating the public IP address of the remote device.
	 * 
	 * @param String
	 *            new remoteAddress property value
	 */
	public void setDestinationAddress(String address) {
		this.destinationAddress = address;
	}

	/**
	 * This method returns the GRETunnelConfiguration.key property value. This property is described as follows:
	 * 
	 * An integer indicating the ID of the GRETunnel.
	 * 
	 * @return String current destinationeAddress property value
	 */
	public int getKey() {
		return this.key;
	}

	/**
	 * This method sets the GRETunnelConfiguration.key property value. This property is described as follows:
	 * 
	 * An integer indicating the ID of the GRETunnel.
	 * 
	 * @param int new key property value
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * Adds a new GRETunnelServiceConfiguration association between the given GRETunnelService and this element.
	 * 
	 * @param greTunnelService
	 */
	public void setGRETunnelService(GRETunnelService greTunnelService) {
		if (greTunnelService != null)
			GRETunnelServiceConfiguration.link(this, greTunnelService);
	}

	/**
	 * Removes the GRETunnelServiceConfiguration association between the given GreTunnelService and this element.
	 * 
	 * @param greTunnelService
	 */
	public void unsetGRETunnelService(GRETunnelService greTunnelService) {
		if (greTunnelService != null) {
			Association a = this.getFirstFromAssociationByTypeAndElement(GRETunnelServiceConfiguration.class, greTunnelService);
			if (a != null)
				a.unlink();
		}
	}

	/**
	 * 
	 * Returns the GRETunnelService associated to this element.
	 * 
	 * @return
	 */
	public GRETunnelService getGRETunnelService() {
		return (GRETunnelService) this.getFirstFromAssociatedElementByType(GRETunnelServiceConfiguration.class);
	}
}