package net.i2cat.mantychore.network.model.topology;

import java.util.List;

import net.i2cat.mantychore.network.model.domain.AdministrativeDomain;
import net.i2cat.mantychore.network.model.physical.Location;

/**
 * A network element is an abstract class which describe an elements in a computer network.
 * 
 * @author isart
 *
 */
public abstract class NetworkElement {
	String name; 
	
	Location LocatedAt;
	
	List<AdministrativeDomain> inAdminDomains;

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
		return LocatedAt;
	}

	public void setLocatedAt(Location locatedAt) {
		LocatedAt = locatedAt;
	}
}
