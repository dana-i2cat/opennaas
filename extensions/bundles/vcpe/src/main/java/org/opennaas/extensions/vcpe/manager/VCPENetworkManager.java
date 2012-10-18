package org.opennaas.extensions.vcpe.manager;

import java.util.List;

import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class VCPENetworkManager implements IVCPENetworkManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#create(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public Boolean create(VCPENetworkModel vcpeNetworkModel) {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetManager#removed(java.lang.String)
	 */
	@Override
	public Boolean remove(String vcpeNetworkId) {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetManager#getVCPENetworkById(java.lang.String)
	 */
	@Override
	public VCPENetworkModel getVCPENetworkById(String vcpeNetworkId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetManager#getAllVCPENetworks()
	 */
	@Override
	public List<VCPENetworkModel> getAllVCPENetworks() {
		// TODO Auto-generated method stub
		return null;
	}

}
