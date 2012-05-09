package org.opennaas.core.resources.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class ResourceHelper {

	static Log	log	= LogFactory
							.getLog(ResourceHelper.class);

	/*
	 * Protocol Session Manager helpers
	 */

	/**
	 * Configure protocol session context
	 */
	public static ProtocolSessionContext newSessionContextNetconf() {
		String uri = "mock://user:pass@host.net:2212/mocksubsystem";

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;

	}

	/**
	 * It creates a new resource descriptor
	 * 
	 * @param type
	 *            resource descriptor type
	 * @return
	 */
	public static ResourceDescriptor newResourceDescriptor(String type) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		capabilityDescriptors.add(newChassisCapabilityDescriptor());
		capabilityDescriptors.add(newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		/* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		resourceDescriptor.setProperties(properties);
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(type);
		information.setName("Junos Test");
		resourceDescriptor.setInformation(information);

		return resourceDescriptor;
	}

	/**
	 * Create a new resource descriptor of an arbitrary type.
	 * 
	 * @param type
	 *            type of resource descriptor.
	 * @return a descriptor
	 */
	public static ResourceDescriptor newResourceDescriptorProteus(String type) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		capabilityDescriptors.add(newCapabilityDescriptorProteus("connections"));
		capabilityDescriptors.add(newCapabilityDescriptorProteus("queue"));

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		/* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		resourceDescriptor.setProperties(properties);
		ResourceIdentifier identifier = new ResourceIdentifier(type);
		resourceDescriptor.setId(identifier.getId());
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(type);
		information.setName("Switch Test");
		resourceDescriptor.setInformation(information);

		return resourceDescriptor;
	}

	/**
	 * Create a new resource descriptor of an arbitrary type.
	 * 
	 * @param type
	 *            type of resource descriptor.
	 * @return a descriptor
	 */
	public static ResourceDescriptor newResourceDescriptorNetwork(String name) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		capabilityDescriptors.add(newCapabilityDescriptorNetwork("layer3"));
		capabilityDescriptors.add(newCapabilityDescriptorNetwork("queue"));

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		/* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		resourceDescriptor.setProperties(properties);
		ResourceIdentifier identifier = new ResourceIdentifier("network");
		resourceDescriptor.setId(identifier.getId());
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType("network");
		information.setName(name);
		resourceDescriptor.setInformation(information);

		return resourceDescriptor;
	}

	/**
	 * Create a Capability Descriptor of an arbitrary type but proteus/1.0.
	 * 
	 * @param type
	 * @return a capability
	 */
	public static CapabilityDescriptor newCapabilityDescriptorProteus(String type) {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, "proteus");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, "1.0");
		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType(type);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;
	}

	public static CapabilityDescriptor newChassisCapabilityDescriptor() {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		// TODO IS IT EXIT A BETTER METHOD TO PASS THE URI
		String uri = "mock://user:pass@host.net:2212/mocksubsystem";

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.PROTOCOL_URI, uri);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, "junos");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, "10.10");
		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType("chassis");
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;
	}

	public static CapabilityDescriptor newQueueCapabilityDescriptor() {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		// TODO IS IT EXIT A BETTER METHOD TO PASS THE URI
		String uri = "mock://user:pass@host.net:2212/mocksubsystem";

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.PROTOCOL_URI, uri);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, "junos");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, "10.10");
		capabilityDescriptor.getCapabilityProperties().add(property);
		Information capabilityInformation = new Information();
		capabilityInformation.setType("queue");
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;
	}

	public static CapabilityDescriptor newCapabilityDescriptorNetwork(String type) {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		// TODO IS IT EXIT A BETTER METHOD TO PASS THE URI
		String uri = "mock://user:pass@host.net:2212/mocksubsystem";

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.PROTOCOL_URI, uri);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, "network");
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, "1.0");
		capabilityDescriptor.getCapabilityProperties().add(property);

		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType(type);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;
	}

	/**
	 * It creates a new resource descriptor
	 * 
	 * @param capabilities
	 * @param type
	 * @param uri
	 * @return ResourceDescriptor
	 */
	public static ResourceDescriptor newResourceDescriptor(List<CapabilityDescriptor> capabilityDescriptors,
			String type, String uri, String infoName) {

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();

		// Add capabilities
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		// Add properties
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				uri);
		resourceDescriptor.setProperties(properties);

		// Add information. It is only necessary to add type
		Information information = new Information();
		information.setType(type);
		information.setName(infoName);
		resourceDescriptor.setInformation(information);

		return resourceDescriptor;
	}

	/**
	 * It creates a new capability descriptor
	 * 
	 * @param capabilities
	 * @param type
	 * @param uri
	 * @return CapabilityDescriptor
	 */
	public static CapabilityDescriptor newCapabilityDescriptor(String actionName, String version, String type, String uri) {

		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.PROTOCOL_URI, uri);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, actionName);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, version);
		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType(type);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;
	}

}
