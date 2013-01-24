/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public class VCPEMultipleProvider implements IVCPENetworkBuilder {

	private Log	log	= LogFactory.getLog(VCPEMultipleProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder#build(org.opennaas.core.resources.IResource,
	 * org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public VCPENetworkModel build(IResource vcpe, VCPENetworkModel vcpeNetworkModel) throws ResourceException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.capability.builder.builders.IVCPENetworkBuilder#destroy(org.opennaas.core.resources.IResource,
	 * org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public VCPENetworkModel destroy(IResource resource, VCPENetworkModel vcpeNetworkModel) throws ResourceException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
