package org.opennaas.extensions.vcpe.manager;

import java.io.IOException;
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
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability;
import org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilderCapability;
import org.opennaas.extensions.vcpe.capability.ip.VCPEIPCapability;
import org.opennaas.extensions.vcpe.capability.vrrp.VCPEVRRPCapability;
import org.opennaas.extensions.vcpe.manager.isfree.IsFreeChecker;
import org.opennaas.extensions.vcpe.manager.model.VCPEManagerModel;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.manager.templates.TemplateSelector;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class VCPENetworkManager implements IVCPENetworkManager {

	public static final String	RESOURCE_VCPENET_TYPE	= "vcpenet";
	private VCPEManagerModel	model;

	/**
	 * @throws IOException
	 */
	public VCPENetworkManager() throws IOException {
		initModel();
	}

	@Override
	public VCPEManagerModel getModel() throws VCPENetworkManagerException {
		return model;
	}

	public void setModel(VCPEManagerModel model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#create(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public String create(VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException {
		// Create the resource
		IResource resource = createResource(vcpeNetworkModel.getName());
		vcpeNetworkModel.setId(resource.getResourceIdentifier().getId());
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

	/**
	 * @return the physical infrastructure
	 * @throws VCPENetworkManagerException
	 */
	@Override
	public VCPENetworkModel getPhysicalInfrastructureSuggestion(String templateType) throws VCPENetworkManagerException {
		ITemplate template = TemplateSelector.getTemplate(templateType);
		VCPENetworkModel phySuggestion = template.getPhysicalInfrastructureSuggestion();
		return phySuggestion;
	}

	/**
	 * @return a suggestion of the logical infrastructure
	 * @throws VCPENetworkManagerException
	 */
	@Override
	public VCPENetworkModel getLogicalInfrastructureSuggestion(VCPENetworkModel physical) throws VCPENetworkManagerException {
		ITemplate template = TemplateSelector.getTemplate(physical.getTemplateType());
		VCPENetworkModel suggestion = template.getLogicalInfrastructureSuggestion(physical);
		return suggestion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isVLANFree(java.lang.String)
	 */
	@Override
	public Boolean isVLANFree(String vcpeId, String router, String vlan, String ifaceName) throws VCPENetworkManagerException {
		return IsFreeChecker.isVLANFree(vcpeId, router, vlan, ifaceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isInterfaceFree(java.lang.String)
	 */
	@Override
	public Boolean isInterfaceFree(String vcpeId, String router, String ifaceName) throws VCPENetworkManagerException {
		return IsFreeChecker.isInterfaceFree(vcpeId, router, ifaceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#isIPFree(java.lang.String)
	 */
	@Override
	public Boolean isIPFree(String vcpeId, String router, String ip) throws VCPENetworkManagerException {
		return IsFreeChecker.isIPFree(vcpeId, router, ip);
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
			throw new VCPENetworkManagerException("Can't create the resource. "
					+ e.getMessage() != null ? e.getMessage() : "");
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
			throw new VCPENetworkManagerException("Can't start the resource. "
					+ e.getMessage() != null ? e.getMessage() : "");
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
			ITemplate template = TemplateSelector.getTemplate(vcpeNetworkModel.getTemplateType());
			VCPENetworkModel model = template.buildModel(vcpeNetworkModel);
			resource = Activator.getResourceManagerService()
					.getResourceById(vcpeNetworkModel.getId());
			// Execute the capability and generate the real environment
			IVCPENetworkBuilderCapability vcpeNetworkBuilderCapability = (IVCPENetworkBuilderCapability) resource
					.getCapabilityByInterface(IVCPENetworkBuilderCapability.class);
			vcpeNetworkBuilderCapability.buildVCPENetwork(model);
		} catch (Exception e) {
			if (resource != null) {
				stopResource(vcpeNetworkModel.getId());
				removeResource(vcpeNetworkModel.getId());
			}
			throw new VCPENetworkManagerException("Can't build the environment of the resource. "
					+ e.getMessage() != null ? e.getMessage() : "");
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
			throw new VCPENetworkManagerException("Can't stop the resource. "
					+ e.getMessage() != null ? e.getMessage() : "");
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
			throw new VCPENetworkManagerException("Can't remove the resource. "
					+ e.getMessage() != null ? e.getMessage() : "");
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
			IVCPENetworkBuilderCapability vcpeNetworkBuilderCapability = (IVCPENetworkBuilderCapability) resource
					.getCapabilityByInterface(IVCPENetworkBuilderCapability.class);
			vcpeNetworkBuilderCapability.destroyVCPENetwork();
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
		capabs.add(getVRRPCapability());
		capabs.add(getIPCapability());
		descriptor.setCapabilityDescriptors(capabs);
		return descriptor;
	}

	/**
	 * Get the builder capability of the VCPENetwork to create the resource
	 * 
	 * @return CapabilityDescriptor
	 */
	private CapabilityDescriptor getBuilderCapability() {
		return getCapabilityDescriptor(VCPENetworkBuilderCapability.CAPABILITY_TYPE);
	}

	/**
	 * Get the VRRP capability of the VCPENetwork
	 * 
	 * @return CapabilityDescriptor
	 */
	private CapabilityDescriptor getVRRPCapability() {
		return getCapabilityDescriptor(VCPEVRRPCapability.CAPABILITY_TYPE);
	}

	/**
	 * Get the VRRP capability of the VCPENetwork
	 * 
	 * @return CapabilityDescriptor
	 */
	private CapabilityDescriptor getIPCapability() {
		return getCapabilityDescriptor(VCPEIPCapability.CAPABILITY_TYPE);
	}

	/**
	 * Creates a CapabilityDescriptor for a capability of given capabilityType
	 * 
	 * @param capabilityType
	 * @return CapabilityDescriptor
	 */
	private CapabilityDescriptor getCapabilityDescriptor(String capabilityType) {
		CapabilityDescriptor desc = new CapabilityDescriptor();
		Information info = new Information();
		info.setType(capabilityType);
		desc.setCapabilityInformation(info);
		return desc;
	}

	// TODO this method should go to the Bootstrapper and VCPENetworkManager to be a Resource
	// TODO the IOException thrown would then prevent the resource to start
	/**
	 * @throws IOException
	 */
	private void initModel() throws IOException {
		VCPEManagerModel model = new VCPEManagerModel();
		PhysicalInfrastructureLoader loader = new PhysicalInfrastructureLoader();
		model.setPhysicalInfrastructure(loader.loadPhysicalInfrastructure());
		setModel(model);
	}

}
