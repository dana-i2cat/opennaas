package net.i2cat.nexus.resources.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceBootstrapper;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.CapabilityProperty;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.resources.profile.IProfile;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockResource implements IResource {
	static Log					log				= LogFactory
														.getLog(MockResource.class);

	Map<String, ICapability>	capabilities	= new HashMap<String, ICapability>();
	ManagedElement				model;
	ResourceDescriptor			resourceDescriptor;
	List<CapabilityDescriptor>	capabilityDescriptors;

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

	}

	public String getResourceId() {
		return resourceDescriptor.getId();
	}

	public void setResourceId(String resourceId) {
		resourceDescriptor.setId(resourceId);
	}

	public void addCapability(ICapability capability) {
		log.info("add Capability...");
		capabilities.put(capability.getCapabilityInformation().getName(),
				capability);

	}

	public ICapability removeCapability(Information info) {
		return capabilities.remove(info.getName());
	}

	public ICapability getCapability(Information info) {
		log.info("get Capability...");
		return capabilities.get(info.getName());
	}

	public ManagedElement getModel() {
		log.info("get Model...");
		return model;
	}

	public void setModel(ManagedElement model) {
		log.info("set Model...");
		this.model = model;

	}

	public void activate() throws ResourceException {
		log.info("Activate...");

	}

	public void deactivate() throws ResourceException {
		log.info("Deactivate...");

	}

	public void initialize() throws ResourceException {
		log.info("Initialize...");

	}

	public State getState() {
		log.info("get State...");
		return null;
	}

	public void setState(State arg0) {
		log.info("set State...");

	}

	public void shutdown() throws ResourceException {
		log.info("shutdown...");

	}

	public List<ICapability> getCapabilities() {
		log.info("get Capabilities...");
		return null;
	}

	public ResourceDescriptor getResourceDescriptor() {
		log.info("get Resource Descriptor...");
		return resourceDescriptor;
	}

	public IResourceIdentifier getResourceIdentifier() {
		log.info("get Resource Identifier...");
		return null;
	}

	public void setCapabilities(List<ICapability> arg0) {
		log.info("set Capabilities...");

	}

	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
		log.info("set Resource Descriptor...");
		this.resourceDescriptor = resourceDescriptor;

	}

	public void setResourceIdentifier(IResourceIdentifier arg0) {
		log.info("set Resource Identifier...");

	}

	public void start() throws ResourceException {
		log.info("Start...");

	}

	public void stop() throws ResourceException {
		log.info("Stop...");

	}

	public void setProfile(IProfile profile) {

	}

	public IProfile getProfile() {
		return null;
	}

	@Override
	public IResourceBootstrapper getBootstrapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBootstrapper(IResourceBootstrapper bootstrapper) {
		// TODO Auto-generated method stub

	}

}
