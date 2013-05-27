package org.opennaas.core.resources.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class MockResource extends Resource {
	static Log					log				= LogFactory
														.getLog(MockResource.class);

	Map<String, ICapability>	capabilities	= new HashMap<String, ICapability>();
	IModel						model;
	ResourceDescriptor			resourceDescriptor;
	List<CapabilityDescriptor>	capabilityDescriptors;
	IResourceIdentifier			resourceIdentifier;
	IResourceBootstrapper		bootstrapper;

	public static CapabilityDescriptor createCapabilityDescriptor(
			String typeCapability) {

		return createCapabilityDescriptor(typeCapability, "no_idea_was_this_does_but_some_tests_dont_want_to_specify_it");
	}

	public static CapabilityDescriptor createCapabilityDescriptor(
			String typeCapability, String actionCapability) {

		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		// TODO IS IT EXIT A BETTER METHOD TO PASS THE URI
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("")) {
			log.info("INFO: Getting uri param from terminal");
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.PROTOCOL_URI, uri);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, "junos");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, "10.10");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(ProtocolSessionContext.PROTOCOL,
				"netconf");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_CAPABILITY, actionCapability);
		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType(typeCapability);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;

	}

	public MockResource() {
		resourceDescriptor = new ResourceDescriptor();
		capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceIdentifier = new ResourceIdentifier();
	}

	public String getResourceId() {
		return resourceDescriptor.getId();
	}

	public void setResourceId(String resourceId) {
		resourceDescriptor.setId(resourceId);
	}

	@Override
	public void addCapability(ICapability capability) {
		log.info("add Capability...");
		capabilities.put(capability.getCapabilityInformation().getType(),
				capability);
	}

	@Override
	public ICapability removeCapability(Information info) {
		return capabilities.remove(info.getType());
	}

	@Override
	public ICapability getCapability(Information info) {
		log.info("get Capability...");
		return capabilities.get(info.getType());
	}

	@Override
	public List<ICapability> getCapabilities() {
		log.info("get Capabilities...");

		ArrayList<ICapability> capabs = new ArrayList<ICapability>();
		for (ICapability capab : capabilities.values()) {
			capabs.add(capab);
		}
		return capabs;
	}

	@Override
	public void setCapabilities(List<? extends ICapability> capabs) {
		log.info("set Capabilities...");

		for (ICapability capab : capabs) {
			addCapability(capab);
		}
	}

	@Override
	public List<ICapability> getCapabilitiesByInterface(Class<? extends ICapability> interfaze) {
		List<ICapability> filteredCapabilities = new ArrayList<ICapability>();
		for (ICapability capability : getCapabilities()) {
			if (interfaze.isInstance(capability)) {
				filteredCapabilities.add(capability);
			}
		}
		return filteredCapabilities;
	}

	@Override
	public ICapability getCapabilityByInterface(Class<? extends ICapability> interfaze) throws ResourceException {
		for (ICapability capability : getCapabilities()) {
			if (interfaze.isInstance(capability)) {
				return capability;
			}
		}
		throw new ResourceException("Cannot find capability with interface " + interfaze);
	}

	@Override
	public IModel getModel() {
		log.info("get Model...");
		return model;
	}

	@Override
	public void setModel(IModel model) {
		log.info("set Model...");
		this.model = model;

	}

	@Override
	public void activate() throws ResourceException {
		log.info("Activate...");

	}

	@Override
	public void deactivate() throws ResourceException {
		log.info("Deactivate...");

	}

	@Override
	public void initialize() throws ResourceException {
		log.info("Initialize...");

	}

	@Override
	public State getState() {
		log.info("get State...");
		return null;
	}

	@Override
	public void setState(State arg0) {
		log.info("set State...");

	}

	@Override
	public void shutdown() throws ResourceException {
		log.info("shutdown...");

	}

	@Override
	public ResourceDescriptor getResourceDescriptor() {
		log.info("get Resource Descriptor...");
		return resourceDescriptor;
	}

	@Override
	public IResourceIdentifier getResourceIdentifier() {
		log.info("get Resource Identifier...");
		return resourceIdentifier;
	}

	@Override
	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
		log.info("set Resource Descriptor...");
		this.resourceDescriptor = resourceDescriptor;

	}

	@Override
	public void setResourceIdentifier(IResourceIdentifier identifier) {
		log.info("set Resource Identifier...");
		resourceIdentifier = identifier;
	}

	@Override
	public void start() throws ResourceException {
		log.info("Start...");

	}

	@Override
	public void stop() throws ResourceException {
		log.info("Stop...");

	}

	@Override
	public void setProfile(IProfile profile) {

	}

	@Override
	public IProfile getProfile() {
		return null;
	}

	@Override
	public IResourceBootstrapper getBootstrapper() {
		return bootstrapper;
	}

	@Override
	public void setBootstrapper(IResourceBootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	@Override
	public ICapability getCapabilityByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}
}
