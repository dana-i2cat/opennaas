package org.opennaas.extensions.vcpe.manager;

import static com.google.common.collect.Iterables.filter;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilder;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.manager.templates.TemplateSelector;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class VCPENetworkManager implements IVCPENetworkManager {

	public static final String	RESOURCE_VCPENET_TYPE	= "vcpenet";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#create(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public String create(VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException {
		// Create the resource
		IResource resource = createResource(vcpeNetworkModel.getVcpeNetworkName());
		vcpeNetworkModel.setVcpeNetworkId(resource.getResourceIdentifier().getId());
		// Start the resource
		startResource(resource.getResourceIdentifier().getId());
		// Build the enviroment
		build(vcpeNetworkModel);
		return resource.getResourceIdentifier().getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#remove(java.lang.String)
	 */
	@Override
	public Boolean remove(String vcpeNetworkId) throws VCPENetworkManagerException {
		boolean isRemoved = true;
		// Destroy environment
		destroy(vcpeNetworkId);
		// Stop the resource
		stopResource(vcpeNetworkId);
		// Remove the resource
		removeResource(vcpeNetworkId);
		return isRemoved;
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
	public Boolean isVLANFree(String vcpeId, String vlan, String ifaceName) throws VCPENetworkManagerException {
		boolean isFree = true;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			List<IResource> vcpes = manager.listResourcesByType(RESOURCE_VCPENET_TYPE);
			for (IResource vcpe : vcpes) {
				if (!vcpe.getResourceIdentifier().getId().equals(vcpeId)) {
					for (Interface iface : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), Interface.class)) {
						if (ifaceName.equals(iface.getPhysicalInterfaceName()) && vlan.equals(String.valueOf(iface.getVlanId()))) {
							isFree = false;
						}
					}
				}
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException("Can't check the VLAN: " + vlan);
		}
		return isFree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isIPFree(java.lang.String)
	 */
	@Override
	public Boolean isIPFree(String vcpeId, String ip) throws VCPENetworkManagerException {
		boolean isFree = true;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			List<IResource> vcpes = manager.listResourcesByType(RESOURCE_VCPENET_TYPE);
			for (IResource vcpe : vcpes) {
				if (!vcpe.getResourceIdentifier().getId().equals(vcpeId)) {
					for (Interface iface : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), Interface.class)) {
						if (ip.equals(iface.getIpAddress())) {
							isFree = false;
						}
					}
				}
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException("Can't check the IP: " + ip);
		}
		return isFree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isInterfaceFree(java.lang.String)
	 */
	@Override
	public Boolean isInterfaceFree(String vcpeId, String ifaceIn) throws VCPENetworkManagerException {
		boolean isFree = true;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			List<IResource> vcpes = manager.listResourcesByType(RESOURCE_VCPENET_TYPE);
			for (IResource vcpe : vcpes) {
				if (!vcpe.getResourceIdentifier().getId().equals(vcpeId)) {
					for (Interface iface : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), Interface.class)) {
						if (ifaceIn.equals(iface.getName())) {
							isFree = false;
						}
					}
				}
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException("Can't check the interface: " + ifaceIn);
		}
		return isFree;
	}

	/**
	 * Create the resource VCPENetwork
	 * 
	 * @param vcpeNetworkName
	 * @return the resource
	 */
	private IResource createResource(String vcpeNetworkName) {
		IResource resource = null;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			resource = manager.createResource(getResourceDescriptor(vcpeNetworkName));
			if (resource == null || resource.getResourceIdentifier() == null) {
				throw new VCPENetworkManagerException("Can't create the resource. ");
			}
		} catch (Exception e) {
			throw new VCPENetworkManagerException("Can't create the resource. " + e.getMessage());
		}
		return resource;
	}

	/**
	 * Start the resource
	 * 
	 * @param resource
	 * @return true if the resource has been started
	 */
	private Boolean startResource(String vcpeNetworId) {
		IResource resource = null;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			resource = manager.getResourceById(vcpeNetworId);
			manager.startResource(resource.getResourceIdentifier());
			if (!resource.getState().equals(ILifecycle.State.ACTIVE)) {
				manager.removeResource(resource.getResourceIdentifier());
				throw new VCPENetworkManagerException("Can't start the resource.");
			}
		} catch (Exception e) {
			removeResource(vcpeNetworId);
			throw new VCPENetworkManagerException("Can't start the resource. " + e.getMessage());
		}
		return true;
	}

	/**
	 * Build the environment from the model
	 * 
	 * @param vcpeNetworkModel
	 * @return true if the environment has been created
	 */
	private Boolean build(VCPENetworkModel vcpeNetworkModel) {
		IResource resource = null;
		try {
			ITemplate template = TemplateSelector.getTemplate(vcpeNetworkModel.getTemplateName());
			VCPENetworkModel model = template.buildModel(vcpeNetworkModel);
			resource = Activator.getResourceManagerService()
					.getResourceById(vcpeNetworkModel.getVcpeNetworkId());
			// Execute the capability and generate the real environment
			IVCPENetworkBuilder vcpeNetworkBuilder = (IVCPENetworkBuilder) resource
					.getCapabilityByInterface(IVCPENetworkBuilder.class);
			vcpeNetworkBuilder.buildVCPENetwork(model);
		} catch (Exception e) {
			if (resource != null) {
				stopResource(vcpeNetworkModel.getVcpeNetworkId());
				removeResource(vcpeNetworkModel.getVcpeNetworkId());
			}
			throw new VCPENetworkManagerException("Can't build the environment of the resource. " + e.getMessage());
		}
		return true;
	}

	/**
	 * Stop the VCPNetwork resource
	 * 
	 * @param vcpeNetworkId
	 * @return true if the resource has been stopped
	 */
	private Boolean stopResource(String vcpeNetworkId) {
		IResource resource = null;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			resource = manager.getResourceById(vcpeNetworkId);
			manager.stopResource(resource.getResourceIdentifier());
		} catch (Exception e) {
			throw new VCPENetworkManagerException("Can't stop the resource. " + e.getMessage());
		}
		return true;
	}

	/**
	 * Remove the resource
	 * 
	 * @param vcpeNetworkId
	 * @return true if the resource has been removed
	 */
	private Boolean removeResource(String vcpeNetworkId) {
		IResource resource = null;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			resource = manager.getResourceById(vcpeNetworkId);
			manager.removeResource(resource.getResourceIdentifier());
		} catch (Exception e) {
			throw new VCPENetworkManagerException("Can't remove the resource. " + e.getMessage());
		}
		return true;
	}

	/**
	 * Destroy the environment
	 * 
	 * @param vcpeNetworkId
	 * @return true if the environment has been destroyed
	 * @throws VCPENetworkManagerException
	 */
	private Boolean destroy(String vcpeNetworkId) {
		IResource resource = null;
		try {
			resource = Activator.getResourceManagerService().getResourceById(vcpeNetworkId);
			// Execute the capability and destroy the real enviroment
			IVCPENetworkBuilder vcpeNetworkBuilder = (IVCPENetworkBuilder) resource
					.getCapabilityByInterface(IVCPENetworkBuilder.class);
			vcpeNetworkBuilder.destroyVCPENetwork();
		} catch (Exception e) {
			stopResource(vcpeNetworkId);
			removeResource(vcpeNetworkId);
			throw new VCPENetworkManagerException(e.getMessage());
		}
		return true;
	}

	/**
	 * Get the descriptor with his capability to create the resource
	 * 
	 * @param params
	 * @return VCPENetworkDescriptor
	 */
	private VCPENetworkDescriptor getResourceDescriptor(String resourceName) {
		VCPENetworkDescriptor descriptor = new VCPENetworkDescriptor();
		Information information = new Information();
		information.setType(RESOURCE_VCPENET_TYPE);
		information.setName(resourceName);
		descriptor.setInformation(information);
		// Capability Builder
		List<CapabilityDescriptor> capabs = new ArrayList<CapabilityDescriptor>();
		capabs.add(getBuilderCapability());
		descriptor.setCapabilityDescriptors(capabs);
		return descriptor;
	}

	/**
	 * Get the builder capability of the VCPENetwork to create the resource
	 * 
	 * @return CapabilityDescriptor
	 */
	private CapabilityDescriptor getBuilderCapability() {
		CapabilityDescriptor desc = new CapabilityDescriptor();
		Information info = new Information();
		info.setType(VCPENetworkBuilder.CAPABILITY_TYPE);
		desc.setCapabilityInformation(info);
		return desc;
	}

}
