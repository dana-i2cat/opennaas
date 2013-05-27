package org.opennaas.extensions.vcpe.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.opennaas.extensions.vcpe.model.routing.RoutingConfiguration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ LogicalRouter.class, PhysicalRouter.class })
public class Router extends VCPENetworkElement {

	@XmlIDREF
	protected List<Interface>		interfaces;

	protected RoutingConfiguration	routingConfiguration;

	public List<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	public RoutingConfiguration getRoutingConfiguration() {
		return routingConfiguration;
	}

	public void setRoutingConfiguration(RoutingConfiguration routingConfiguration) {
		this.routingConfiguration = routingConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Router other = (Router) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;
		if (routingConfiguration == null) {
			if (other.routingConfiguration != null)
				return false;
		} else if (!routingConfiguration.equals(other.routingConfiguration))
			return false;

		return true;
	}

}
