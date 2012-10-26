/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates;

import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public interface ITemplate {

	/**
	 * Generate the model from the template and the parameters of the vcpeNetworkModel
	 * 
	 * @param initialModel
	 * @return VCPENetworkModel
	 */
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel);

}
