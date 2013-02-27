/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.templates.mp;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * @author Jordi
 */
public class MultipleProviderTemplate implements ITemplate {

	private String	templateType	= ITemplate.MP_VCPE_TEMPLATE;

	/**
	 * @throws VCPENetworkManagerException
	 * 
	 */
	public MultipleProviderTemplate() throws VCPENetworkManagerException {
	}

	public String getTemplateType() {
		return templateType;
	}

	/**
	 * Generate the model
	 * 
	 * @return VCPENetworkModel
	 */
	@Override
	public VCPENetworkModel buildModel(VCPENetworkModel initialModel) throws VCPENetworkManagerException {
		// TODO Auto-generated method stub
		throw new VCPENetworkManagerException("Unsupported Operation");
	}

	@Override
	public VCPENetworkModel getPhysicalInfrastructureSuggestion() throws VCPENetworkManagerException {
		// TODO Auto-generated method stub
		throw new VCPENetworkManagerException("Unsupported Operation");
	}

	@Override
	public VCPENetworkModel getLogicalInfrastructureSuggestion(VCPENetworkModel physicalInfrastructure) {
		// TODO Auto-generated method stub
		throw new VCPENetworkManagerException("Unsupported Operation");
	}

}
