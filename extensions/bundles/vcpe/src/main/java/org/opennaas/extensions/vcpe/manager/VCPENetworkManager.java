package org.opennaas.extensions.vcpe.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.core.security.acl.IACLManager;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability;
import org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilderCapability;
import org.opennaas.extensions.vcpe.capability.ip.VCPEIPCapability;
import org.opennaas.extensions.vcpe.capability.vrrp.VCPEVRRPCapability;
import org.opennaas.extensions.vcpe.manager.isfree.IsFreeChecker;
import org.opennaas.extensions.vcpe.manager.model.VCPEManagerModel;
import org.opennaas.extensions.vcpe.manager.templates.ITemplate;
import org.opennaas.extensions.vcpe.manager.templates.TemplateSelector;
import org.opennaas.extensions.vcpe.manager.templates.mp.TemplateConstants;
import org.opennaas.extensions.vcpe.model.BGP;
import org.opennaas.extensions.vcpe.model.IPNetworkDomain;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.springframework.security.access.AccessDeniedException;

public class VCPENetworkManager implements IVCPENetworkManager {

	private Log								log						= LogFactory.getLog(VCPENetworkManager.class);

	public static final String				RESOURCE_VCPENET_TYPE	= "vcpenet";
	private VCPEManagerModel				model;

	private ExecutorService					executor;
	private Map<String, Future<Boolean>>	futures;

	/**
	 * @throws IOException
	 */
	public VCPENetworkManager() throws IOException {
		initModel();
		executor = Executors.newSingleThreadExecutor();
		futures = new HashMap<String, Future<Boolean>>();
	}

	/**
	 * 
	 */
	public void destroy() {
		executor.shutdown();
		if (!executor.isTerminated()) {
			executor.shutdownNow();
		}
	}

	/**
	 * @param model
	 */
	public void setModel(VCPEManagerModel model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#getModel()
	 */
	@Override
	public VCPEManagerModel getModel() throws VCPENetworkManagerException {
		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#create(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public String create(VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException {
		log.info("Creating new VCPE: " + vcpeNetworkModel.getName());
		// Create the resource
		IResource resource = createResource(vcpeNetworkModel.getName());
		vcpeNetworkModel.setId(resource.getResourceIdentifier().getId());

		// Secure vCPE Resource for users
		try {
			secureVCPE(resource, vcpeNetworkModel);
		} catch (Exception e) {
			log.error("Error securing vCPE", e);
			// error, remove resource
			removeResource(resource.getResourceIdentifier().getId());
			throw new VCPENetworkManagerException("Error securing vCPE: " + e.getMessage());
		}

		// Start the resource
		startResource(resource.getResourceIdentifier().getId());

		BuildVCPECallable c = new BuildVCPECallable(vcpeNetworkModel);
		Future<Boolean> future = executor.submit(c);
		futures.put(resource.getResourceIdentifier().getId(), future);
		//TODO find a way to execute rollback if something fails

		return resource.getResourceIdentifier().getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#update(org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public String update(VCPENetworkModel partialModel) throws VCPENetworkManagerException {
		log.info("Updating a VCPE: " + partialModel.getName());
		VCPENetworkModel newModel;
		String vcpeId = null;
		try {
			newModel = getVCPENetworkById(partialModel.getId()).deepCopy();
			newModel = fillModel(partialModel, newModel);
			log.info("First remove a VCPE: " + partialModel.getName());
			remove(partialModel.getId());
			log.info("Create the new VCPE: " + partialModel.getName());
			vcpeId = create(newModel);
		} catch (SerializationException e) {
			log.error("can't update the vcpe");
			throw new VCPENetworkManagerException(e);
		}
		return vcpeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#remove(java.lang.String)
	 */
	@Override
	public Boolean remove(String vcpeNetworkId) throws VCPENetworkManagerException {
		log.info("Removing VCPE: " + vcpeNetworkId);
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
		return (VCPENetworkModel) getVCPENetworkByIdSecured(vcpeNetworkId).getModel();
	}

	private IResource getVCPENetworkByIdSecured(String vcpeNetworkId) throws VCPENetworkManagerException {
		if (!getAclManager().isResourceAccessible(vcpeNetworkId))
			throw new VCPENetworkManagerException("Access denied to resource: " + vcpeNetworkId);

		return doGetVCPENetworkById(vcpeNetworkId);
	}

	private IResource doGetVCPENetworkById(String vcpeNetworkId) throws VCPENetworkManagerException {
		IResource resource = null;
		try {
			resource = getResourceManager().getResourceById(vcpeNetworkId);
			if (resource == null) {
				throw new VCPENetworkManagerException("Don't find a VCPENetwork with id = " + vcpeNetworkId);
			}
		} catch (ResourceException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		}
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetManager#getAllVCPENetworks()
	 */
	@Override
	public List<VCPENetworkModel> getAllVCPENetworks() throws VCPENetworkManagerException {

		List<IResource> vcpes = doGetAllVCPENetworks();

		for (int i = vcpes.size() - 1; i >= 0; i--) {
			if (!getAclManager().isResourceAccessible(vcpes.get(i).getResourceIdentifier().getId())) {
				log.debug("Access denied to resource " + vcpes.get(i).getResourceIdentifier().getId());
				vcpes.remove(i);
				log.debug("Resource removed from returnObject.");
			}
		}

		return getModelsFromVCPEs(vcpes);
	}

	/**
	 * Assumes given resources are VCPEs.
	 * 
	 * @param resources
	 * @return
	 * @throws VCPENetworkManagerException
	 */
	private List<VCPENetworkModel> getModelsFromVCPEs(List<IResource> resources) throws VCPENetworkManagerException {
		List<VCPENetworkModel> result = new ArrayList<VCPENetworkModel>();
		for (int i = 0; i < resources.size(); i++) {
			if (resources.get(i).getModel() != null) {
				result.add((VCPENetworkModel) resources.get(i).getModel());
			}
		}
		return result;
	}

	private List<IResource> doGetAllVCPENetworks() throws VCPENetworkManagerException {
		return getResourceManager().listResourcesByType(VCPENetworkDescriptor.RESOURCE_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#getPhysicalInfrastructureSuggestion(java.lang.String)
	 */
	@Override
	public VCPENetworkModel getPhysicalInfrastructureSuggestion(String templateType) throws VCPENetworkManagerException {
		ITemplate template = TemplateSelector.getTemplate(templateType);
		VCPENetworkModel phySuggestion = template.getPhysicalInfrastructureSuggestion();
		return phySuggestion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#getLogicalInfrastructureSuggestion(org.opennaas.extensions.vcpe.model.VCPENetworkModel
	 * )
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#hasFinishedBuild(java.lang.String)
	 */
	@Override
	public boolean hasFinishedBuild(String resourceId) throws VCPENetworkManagerException {
		Future<Boolean> f = futures.get(resourceId);
		if (f == null) {
			throw new VCPENetworkManagerException("No building task for resource " + resourceId);
		}
		return f.isDone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#getBuildResult(java.lang.String)
	 */
	@Override
	public boolean getBuildResult(String resourceId) throws VCPENetworkManagerException {
		if (!hasFinishedBuild(resourceId)) {
			throw new VCPENetworkManagerException("Build task has not yet finished.");
		}

		Future<Boolean> f = futures.get(resourceId);
		if (f == null) {
			throw new VCPENetworkManagerException("No building task for resource " + resourceId);
		}

		boolean result;
		try {
			result = f.get();
		} catch (InterruptedException e) {
			log.error("Creation of VCPE has been interrupted", e);
			throw new VCPENetworkManagerException("Creation of VCPE has been interrupted: " + e.getMessage());
		} catch (ExecutionException e) {
			if (e.getCause() instanceof VCPENetworkManagerException)
				throw (VCPENetworkManagerException) e.getCause();
			else {
				throw new VCPENetworkManagerException(e.getCause());
			}
		} finally {
			// remove future from pending tasks
			futures.remove(resourceId);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#getUserFilteredVCPEModel(java.lang.String)
	 */
	@Override
	public VCPENetworkModel getUserFilteredVCPEModel(String vcpeNetworkId) {
		log.info("Filtering VCPE " + vcpeNetworkId + " model.");
		VCPENetworkModel filteredModel = null;

		try {
			IResource resource = getVCPENetworkByIdSecured(vcpeNetworkId);
			VCPENetworkModel model = (VCPENetworkModel) resource.getModel();
			filteredModel = filterVCPENetworkModel(model);
		} catch (ResourceException re) {
			throw new VCPENetworkManagerException(re.getMessage());
		} catch (AccessDeniedException ad) {
			throw new VCPENetworkManagerException(ad.getMessage());
		} catch (SerializationException se) {
			throw new VCPENetworkManagerException(se.getMessage());
		}

		log.info("VCPE " + vcpeNetworkId + " model filtered.");
		return filteredModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.vcpe.manager.IVCPENetworkManager#editFilteredVCPE(java.lang.String,
	 * org.opennaas.extensions.vcpe.model.VCPENetworkModel)
	 */
	@Override
	public VCPENetworkModel editFilteredVCPE(String vcpeNetworkId, VCPENetworkModel filteredModel) {
		log.info("Updating VCPE " + vcpeNetworkId + " model.");
		VCPENetworkModel updatedModel = new VCPENetworkModel();
		try {

			IResource vcpeResource = getVCPENetworkByIdSecured(vcpeNetworkId);
			VCPENetworkModel oldModel = (VCPENetworkModel) vcpeResource.getModel();
			updatedModel = updateVCPEModelInformation(oldModel, filteredModel);

		} catch (ResourceException re) {
			throw new VCPENetworkManagerException(re.getMessage());
		} catch (AccessDeniedException ad) {
			throw new VCPENetworkManagerException(ad.getMessage());
		}

		log.info("VCPE " + vcpeNetworkId + " model updated.");
		return updatedModel;
	}

	/**
	 * Create the resource VCPENetwork
	 * 
	 * @param vcpeNetworkName
	 * @return the resource
	 */
	private IResource createResource(String vcpeNetworkName) {
		log.debug("Creating new VCPE resource: " + vcpeNetworkName);
		IResource resource = null;
		try {
			resource = getResourceManager().createResource(getResourceDescriptor(vcpeNetworkName));
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
		log.debug("Starting VCPE resource: " + vcpeNetworId);
		IResource resource = null;
		try {
			IResourceManager manager = getResourceManager();
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
		log.debug("Building VCPE resource: " + vcpeNetworkModel.getName());
		IResource resource = null;
		try {
			resource = getResourceManager()
					.getResourceById(vcpeNetworkModel.getId());

			ITemplate template = TemplateSelector.getTemplate(vcpeNetworkModel.getTemplateType());
			VCPENetworkModel model = template.buildModel(vcpeNetworkModel);

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
		log.debug("Stopping VCPE resource: " + vcpeNetworkId);
		IResource resource = null;
		try {
			IResourceManager manager = getResourceManager();
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
		log.debug("Removing VCPE resource: " + vcpeNetworkId);
		IResource resource = null;
		try {
			IResourceManager manager = getResourceManager();
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
		log.debug("Destroying VCPE resource: " + vcpeNetworkId);
		IResource resource = null;
		try {
			resource = getResourceManager().getResourceById(vcpeNetworkId);
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

	/**
	 * @throws IOException
	 */
	private void initModel() throws IOException {
		VCPEManagerModel model = new VCPEManagerModel();
		PhysicalInfrastructureLoader loader = new PhysicalInfrastructureLoader();
		model.setPhysicalInfrastructure(loader.loadPhysicalInfrastructure());
		setModel(model);
	}

	/**
	 * @param oldModel
	 * @param filteredModel
	 * @return
	 * @throws ResourceException
	 * @throws AccessDeniedException
	 */
	private VCPENetworkModel updateVCPEModelInformation(VCPENetworkModel oldModel, VCPENetworkModel filteredModel) throws ResourceException,
			AccessDeniedException {

		updateRoutersInformation(oldModel, filteredModel);
		updateBGPConfiguration(oldModel, filteredModel);
		updateIPRanges(oldModel, filteredModel);

		return oldModel;
	}

	/**
	 * @param oldModel
	 * @param filteredModel
	 */
	private void updateIPRanges(VCPENetworkModel oldModel, VCPENetworkModel filteredModel) {
		if (filteredModel.getClientIpRange() != null && !filteredModel.getClientIpRange().isEmpty())
			oldModel.setClientIpRange(filteredModel.getClientIpRange());

		if (filteredModel.getNocIpRange() != null && !filteredModel.getNocIpRange().isEmpty())
			oldModel.setNocIpRange(filteredModel.getNocIpRange());

	}

	/**
	 * Fill the model to update it if hasn't all the network domains.
	 * 
	 * @param partialModel
	 * @param newModel
	 * @return the complete model
	 */
	private VCPENetworkModel fillModel(VCPENetworkModel partialModel, VCPENetworkModel newModel) {
		// Network Provider1
		if ((IPNetworkDomain) VCPENetworkModelHelper
				.getElementByTemplateName(partialModel, TemplateConstants.WAN1) != null) {
			IPNetworkDomain partialNetwork = (IPNetworkDomain) VCPENetworkModelHelper
					.getElementByTemplateName(partialModel, TemplateConstants.WAN1);
			IPNetworkDomain newNetwork = (IPNetworkDomain) VCPENetworkModelHelper
					.getElementByTemplateName(newModel, TemplateConstants.WAN1);
			fillNetworkDomainModel(partialNetwork, newNetwork);
		}
		if ((IPNetworkDomain) VCPENetworkModelHelper
				.getElementByTemplateName(partialModel, TemplateConstants.WAN2) != null) {
			IPNetworkDomain partialNetwork = (IPNetworkDomain) VCPENetworkModelHelper
					.getElementByTemplateName(partialModel, TemplateConstants.WAN2);
			IPNetworkDomain newNetwork = (IPNetworkDomain) VCPENetworkModelHelper
					.getElementByTemplateName(newModel, TemplateConstants.WAN2);
			fillNetworkDomainModel(partialNetwork, newNetwork);
		}
		if ((IPNetworkDomain) VCPENetworkModelHelper
				.getElementByTemplateName(partialModel, TemplateConstants.LAN_CLIENT) != null) {
			IPNetworkDomain partialNetwork = (IPNetworkDomain) VCPENetworkModelHelper
					.getElementByTemplateName(partialModel, TemplateConstants.LAN_CLIENT);
			IPNetworkDomain newNetwork = (IPNetworkDomain) VCPENetworkModelHelper
					.getElementByTemplateName(newModel, TemplateConstants.LAN_CLIENT);
			fillNetworkDomainModel(partialNetwork, newNetwork);
		}

		return newModel;
	}

	/**
	 * @param partialModel
	 * @param newModel
	 */
	private void fillNetworkDomainModel(IPNetworkDomain partialNetwork, IPNetworkDomain newNetwork) {
		newNetwork.setASNumber(partialNetwork.getASNumber());
		newNetwork.setName(partialNetwork.getName());
		newNetwork.setOwner(partialNetwork.getOwner());
		newNetwork.setInterfaces(partialNetwork.getInterfaces());
		newNetwork.setIPAddressRanges(partialNetwork.getIPAddressRanges());
	}

	/**
	 * @param oldModel
	 * @param filteredModel
	 */
	private void updateBGPConfiguration(VCPENetworkModel oldModel, VCPENetworkModel filteredModel) {
		BGP oldBGP = oldModel.getBgp();
		BGP newBGP = oldModel.getBgp();

		if (newBGP.getClientASNumber() != null && !newBGP.getClientASNumber().equals(oldBGP.getClientASNumber()))
			oldBGP.setClientASNumber(newBGP.getClientASNumber());

		if (newBGP.getNocASNumber() != null && !newBGP.getNocASNumber().equals(oldBGP.getNocASNumber()))
			oldBGP.setNocASNumber(newBGP.getNocASNumber());
	}

	/**
	 * @param oldModel
	 * @param filteredModel
	 * @throws ResourceException
	 * @throws AccessDeniedException
	 */
	private void updateRoutersInformation(VCPENetworkModel oldModel, VCPENetworkModel filteredModel) throws VCPENetworkManagerException,
			ResourceException, AccessDeniedException {

		IResourceManager resourceManager;

		log.debug("Updating routers information.");

		resourceManager = getResourceManager();

		List<Router> routerList = VCPENetworkModelHelper.getRouters(filteredModel.getElements());

		for (Router router : routerList) {

			log.debug("Checking user access to router " + router.getName());
			// this call is done to launch an AccessDeniedException, if necessary
			resourceManager.getResource(
					resourceManager.getIdentifierFromResourceName("router", router.getName()));

			log.debug("Updating router " + router.getName() + " information");

			if (VCPENetworkModelHelper.getRouterByName(oldModel.getElements(), router.getName()) == null)
				throw new ResourceException("No router with name " + router.getName() + " in model.");

			List<Link> routerLinks = VCPENetworkModelHelper.getAllRouterLinksFromModel(filteredModel, router);
			VCPENetworkModelHelper.updateRouterInformation(oldModel, router, routerLinks);

		}

		log.debug("Routers information updated.");

	}

	/**
	 * @param originalModel
	 * @return
	 * @throws SerializationException
	 * @throws ResourceException
	 */
	private VCPENetworkModel filterVCPENetworkModel(VCPENetworkModel originalModel) throws SerializationException, ResourceException {

		IResourceManager resourceManager;
		VCPENetworkModel filteredModel = originalModel.deepCopy();

		resourceManager = getResourceManager();

		List<Router> routerList = VCPENetworkModelHelper.getRouters(originalModel.getElements());

		for (Router router : routerList) {

			log.debug("Cheking user access to router " + router.getName());

			String routerName = router.getName();

			try {
				resourceManager.getResource(
						resourceManager.getIdentifierFromResourceName("router", routerName));

			} catch (AccessDeniedException ad) {
				log.debug("Access denied to router " + router.getName() + ". Removing router from VCPE model.");
				VCPENetworkModelHelper.removeAllRouterInformationFromModel(filteredModel, routerName);
				log.debug("Router " + router.getName() + " removed from VCPE model.");

			}

		}

		return filteredModel;
	}

	private IACLManager getAclManager() throws VCPENetworkManagerException {
		try {
			return Activator.getACLManagerService();
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		}
	}

	private IResourceManager getResourceManager() throws VCPENetworkManagerException {
		try {
			return Activator.getResourceManagerService();
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private class BuildVCPECallable implements Callable<Boolean> {

		VCPENetworkModel	vcpeNetworkModel;

		public BuildVCPECallable(VCPENetworkModel vcpeNetworkModel) {
			this.vcpeNetworkModel = vcpeNetworkModel;
		}

		@Override
		public Boolean call() throws Exception {
			return build(vcpeNetworkModel);
		}

		public VCPENetworkModel getVcpeNetworkModel() {
			return vcpeNetworkModel;
		}
	}

	private void secureVCPE(IResource vcpeNeworkResource, VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException {
		List<String> users = new ArrayList<String>();

		users.add("admin");
		users.add(((IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(vcpeNetworkModel,
				TemplateConstants.LAN_CLIENT)).getOwner());
		users.add(((IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(vcpeNetworkModel,
				TemplateConstants.WAN1)).getOwner());
		users.add(((IPNetworkDomain) VCPENetworkModelHelper.getElementByTemplateName(vcpeNetworkModel,
				TemplateConstants.WAN2)).getOwner());

		for (String user : users) {
			getAclManager().secureResource(vcpeNeworkResource.getResourceIdentifier().getId(), user);
		}
	}
}
