package org.opennaas.extensions.quantum.model;

/**
 * Tenant mixin, add to subclasses that have a tenant
 * 
 * @author Julio Carlos Barrera
 * 
 */
public interface HasTenant {

	public String getTenant_id();

}
