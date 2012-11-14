package org.opennaas.extensions.vcpe.manager;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.manager.templates.TemplateSelector;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class VCPENetworkManager implements IVCPENetworkManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#build(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public Boolean build(VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException {
		try {
			ITemplate template = TemplateSelector.getTemplate(vcpeNetworkModel.getTemplateName());
			VCPENetworkModel model = template.buildModel(vcpeNetworkModel);
			IResource resource = Activator.getResourceManagerService()
					.getResourceById(vcpeNetworkModel.getVcpeNetworkId());
			// Execute the capability and generate the real environment
			IVCPENetworkBuilder vcpeNetworkBuilder = (IVCPENetworkBuilder) resource
					.getCapabilityByInterface(IVCPENetworkBuilder.class);
			vcpeNetworkBuilder.buildVCPENetwork(model);
		} catch (ResourceException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetManager#destroy(java.lang.String)
	 */
	@Override
	public Boolean destroy(String vcpeNetworkId) throws VCPENetworkManagerException {
		try {
			IResource resource = Activator.getResourceManagerService().getResourceById(vcpeNetworkId);
			// Execute the capability and destroy the real enviroment
			IVCPENetworkBuilder vcpeNetworkBuilder = (IVCPENetworkBuilder) resource
					.getCapabilityByInterface(IVCPENetworkBuilder.class);
			vcpeNetworkBuilder.destroyVCPENetwork();
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		} catch (CapabilityException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		} catch (ResourceException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetManager#getVCPENetworkById(java.lang.String)
	 */
	@Override
	public VCPENetworkModel getVCPENetworkById(String vcpeNetworkId) throws VCPENetworkManagerException {
		IResource resource = null;
		try {
			resource = Activator.getResourceManagerService().getResourceById(vcpeNetworkId);
			if (resource == null) {
				throw new VCPENetworkManagerException("Don't find a VCPENetwork with id = " + vcpeNetworkId);
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		} catch (ResourceException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		}
		return (VCPENetworkModel) resource.getModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetManager#getAllVCPENetworks()
	 */
	@Override
	public List<VCPENetworkModel> getAllVCPENetworks() throws VCPENetworkManagerException {
		List<IResource> listModel = null;
		List<VCPENetworkModel> result = new ArrayList<VCPENetworkModel>();
		try {
			listModel = Activator.getResourceManagerService().listResourcesByType(VCPENetworkDescriptor.RESOURCE_TYPE);
			for (int i = 0; i < listModel.size(); i++) {
				if (listModel.get(i).getModel() != null) {
					result.add((VCPENetworkModel) listModel.get(i).getModel());
				}
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isVLANFree(java.lang.String)
	 */
	@Override
	public Boolean isVLANFree(String vlan) throws VCPENetworkManagerException {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isIPFree(java.lang.String)
	 */
	@Override
	public Boolean isIPFree(String ip) throws VCPENetworkManagerException {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isInterfaaceFree(java.lang.String)
	 */
	@Override
	public Boolean isInterfaaceFree(String iface) throws VCPENetworkManagerException {
		// TODO Auto-generated method stub
		return true;
	}

}
