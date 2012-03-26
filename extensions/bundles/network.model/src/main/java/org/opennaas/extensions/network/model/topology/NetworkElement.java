package org.opennaas.extensions.network.model.topology;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.network.model.domain.AdministrativeDomain;
import org.opennaas.extensions.network.model.physical.Location;

/**
 * A network element is an abstract class which describe an elements in a computer network.
 *
 * @author isart
 *
 */
public abstract class NetworkElement {
	String						name;

	Location					locatedAt;

	List<AdministrativeDomain>	inAdminDomains	= new ArrayList<AdministrativeDomain>();

	public List<AdministrativeDomain> getInAdminDomains() {
		return inAdminDomains;
	}

	public void setInAdminDomains(List<AdministrativeDomain> inAdminDomains) {
		this.inAdminDomains = inAdminDomains;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocatedAt() {
		return locatedAt;
	}

	public void setLocatedAt(Location locatedAt) {
		this.locatedAt = locatedAt;
	}
}
