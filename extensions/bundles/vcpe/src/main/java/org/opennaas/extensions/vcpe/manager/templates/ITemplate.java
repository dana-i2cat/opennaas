/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public interface ITemplate {

	/**
	 * Generate the model from the template and the parameters of the initialModel. Uses managerModel to check availability of given parameters.
	 * 
	 * @param initialModel
	 * @param managerModel
	 * @return VCPENetworkModel
	 * @throws VCPENetworkManagerException
	 *             if parameters are not available
	 */
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel) throws VCPENetworkManagerException;

	public VCPENetworkModel getPhysicalInfrastructureSuggestion() throws VCPENetworkManagerException;

	public VCPENetworkModel getLogicalInfrastructureSuggestion(VCPENetworkModel physicalInfrastructure) throws VCPENetworkManagerException;

}
