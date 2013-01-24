/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 * 
 */
public interface IVCPENetworkBuilder {

	/**
	 * Build a vcpe network
	 * 
	 * @param vcpe
	 * @param vcpeNetworkModel
	 * @return VCPENetworkModel
	 * @throws ResourceException
	 */
	public VCPENetworkModel build(IResource vcpe, VCPENetworkModel vcpeNetworkModel) throws ResourceException;

	/**
	 * Destroy a vcpe network
	 * 
	 * @param vcpe
	 * @param vcpeNetworkModel
	 * @return VCPENetworkModel
	 * @throws ResourceException
	 */
	public VCPENetworkModel destroy(IResource resource, VCPENetworkModel vcpeNetworkModel) throws ResourceException;
}
