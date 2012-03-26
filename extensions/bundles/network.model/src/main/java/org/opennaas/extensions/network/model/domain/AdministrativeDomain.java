package org.opennaas.extensions.network.model.domain;

import java.util.List;

import org.opennaas.extensions.network.model.topology.NetworkElement;

/**
 * An entity that acts as a the administrator of a collection of resources.
 * The administrator is the entity who actually controls and provisions the resources.
 * An administrative domain can include computing, visualization, storage and network resources.
 * An Administrative Domain does not say anything about the data plane or the Location.
 * For that, see Network Domain and Location.
 * The administrator enforces policies, and should not be confused with the (economic) owner, who decides on the policy.
 * The administrator and owner of a network element are often, but not always, the same entity!
 * For example, a link between domain 1 and domain 2 is owned by domain 1.
 * So domain 1 effectively decides on the policy of the terminating interfaces of the link.
 * Thus also the interface of this link in domain 2.
 * That interface is than owned by domain 1, but administrated by domain 2.
 * A clear example of this is in so-called “open” optical exchanges.
 * The advantage of administrative domains is that a device including all its interfaces belongs to a single administrative domain.
 * In this RDF description, an Administrative domain only hasNetworkElements of type Device.
 * The Interfaces of the device are implied to reside in the domain due to the hasInterface property.
 * Beside the properties given here, you may want to use the vCard:ORG property to describe the name of the Adminstrator.
 *
 * @author isart
 *
 */
public class AdministrativeDomain {

	List<NetworkElement> domainElements;

	public List<NetworkElement> getDomainElements() {
		return domainElements;
	}

	public void setDomainElements(List<NetworkElement> domainElements) {
		this.domainElements = domainElements;
	}
}
