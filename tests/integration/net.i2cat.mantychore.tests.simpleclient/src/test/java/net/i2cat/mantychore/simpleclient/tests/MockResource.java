package net.i2cat.mantychore.simpleclient.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockResource implements IResource {
	static Logger				log				= LoggerFactory
														.getLogger(MockResource.class);

	Map<String, ICapability>	capabilities	= new HashMap<String, ICapability>();
	ManagedElement				model;
	String						resourceId;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
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
		return null;
	}

	public IResourceIdentifier getResourceIdentifier() {
		log.info("get Resource Identifier...");
		return null;
	}

	public void setCapabilities(List<ICapability> arg0) {
		log.info("get Capabilities...");

	}

	public void setResourceDescriptor(ResourceDescriptor arg0) {
		log.info("set Resource Descriptor...");

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

}
