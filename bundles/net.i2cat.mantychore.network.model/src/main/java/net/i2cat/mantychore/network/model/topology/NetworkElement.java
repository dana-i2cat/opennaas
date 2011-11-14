package net.i2cat.mantychore.network.model.topology;

import net.i2cat.mantychore.network.model.predicates.InAdminDomain;

/**
 * A network element is an abstract class which describe an elements in a computer network.
 * 
 * @author isart
 *
 */
public abstract class NetworkElement {
	
	InAdminDomain inAdminDomain;

	public InAdminDomain getInAdminDomain() {
		return inAdminDomain;
	}

	public void setInAdminDomain(InAdminDomain inAdminDomain) {
		this.inAdminDomain = inAdminDomain;
	}
}
