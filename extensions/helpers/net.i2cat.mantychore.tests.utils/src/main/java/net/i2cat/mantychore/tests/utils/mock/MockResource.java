package net.i2cat.mantychore.tests.utils.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class MockResource implements IResource {
	static Log					log				= LogFactory
														.getLog(MockResource.class);

	Map<String, ICapability>	capabilities	= new HashMap<String, ICapability>();
	IModel						model;
	ResourceDescriptor			resourceDescriptor;
	List<CapabilityDescriptor>	capabilityDescriptors;

	public static CapabilityDescriptor createCapabilityDescriptor(
			String typeCapability) {
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

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_PROTOCOL, "netconf");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(ProtocolSessionContext.PROTOCOL,
				"netconf");
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

	@Override
	public void addCapability(ICapability capability) {
		log.info("add Capability...");
		capabilities.put(capability.getCapabilityInformation().getName(),
				capability);

	}

	@Override
	public ICapability removeCapability(Information info) {
		return capabilities.remove(info.getName());
	}

	@Override
	public ICapability getCapability(Information info) {
		log.info("get Capability...");
		return capabilities.get(info.getName());
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
	public List<ICapability> getCapabilities() {
		log.info("get Capabilities...");
		return null;
	}

	@Override
	public ResourceDescriptor getResourceDescriptor() {
		log.info("get Resource Descriptor...");
		return resourceDescriptor;
	}

	@Override
	public IResourceIdentifier getResourceIdentifier() {
		log.info("get Resource Identifier...");
		return null;
	}

	@Override
	public void setCapabilities(List<ICapability> arg0) {
		log.info("set Capabilities...");

	}

	@Override
	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
		log.info("set Resource Descriptor...");
		this.resourceDescriptor = resourceDescriptor;

	}

	@Override
	public void setResourceIdentifier(IResourceIdentifier arg0) {
		log.info("set Resource Identifier...");

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBootstrapper(IResourceBootstrapper bootstrapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public ICapability getCapabilityByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
