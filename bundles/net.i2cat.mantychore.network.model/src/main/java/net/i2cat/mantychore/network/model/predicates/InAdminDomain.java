package net.i2cat.mantychore.network.model.predicates;

import java.util.List;

import net.i2cat.mantychore.network.model.domain.AdministrativeDomain;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

/**
 * A domain is a group of network elements. The inAdminDomain provides the binding from network elements to an administrative domain. Typically, only Devices or NetworkDomains are related to a AdministrativeDomain. If a Device or NetoworkDomain has interfaces, those are considered to be part of the Administrative Domain as well. If a network element specifies multiple administrative domains, application may expect that each and every of them can fully configure the network element, without conflicts.
 * 
 * @author isart
 *
 */
public class InAdminDomain {
	
	List<NetworkElement> elements;
	AdministrativeDomain domain;
	
	public List<NetworkElement> getElements() {
		return elements;
	}
	public void setElements(List<NetworkElement> elements) {
		this.elements = elements;
	}
	public AdministrativeDomain getDomain() {
		return domain;
	}
	public void setDomain(AdministrativeDomain domain) {
		this.domain = domain;
	}
}
