package net.i2cat.mantychore.network.model.topology;

import java.util.List;

import net.i2cat.mantychore.network.model.domain.AdministrativeDomain;

/**
 * A network element is an abstract class which describe an elements in a computer network.
 * 
 * @author isart
 *
 */
public abstract class NetworkElement {
	
	List<AdministrativeDomain> inAdminDomains;

	public List<AdministrativeDomain> getInAdminDomains() {
		return inAdminDomains;
	}

	public void setInAdminDomains(List<AdministrativeDomain> inAdminDomains) {
		this.inAdminDomains = inAdminDomains;
	}
}
