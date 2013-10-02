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
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

//TODO TO FIX!! WE HAVE TO KNOW HOW A RESOURCE HAVE TO BE FILL
public class ResourceDescriptorFactory {
	public static String	version	= "1.0";

	static Log				log		= LogFactory
											.getLog(ResourceDescriptorFactory.class);

	public static ResourceDescriptor newResourceDescriptor(String name, String type, List<String> capabilities) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		for (String capability : capabilities) {
			capabilityDescriptors.add(newCapabilityDescriptor(capability, "junos"));
		}

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		// /* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		resourceDescriptor.setProperties(properties);

		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(type);
		information.setName(name);
		resourceDescriptor.setInformation(information);
		ResourceIdentifier id = new ResourceIdentifier(type);
		resourceDescriptor.setId(id.getId());

		return resourceDescriptor;
	}

	public static ResourceDescriptor newResourceDescriptorProteus(String resourceName, String resourceType, List<String> capabilities) {

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		for (String capability : capabilities) {
			capabilityDescriptors.add(newCapabilityDescriptorProteus(capability, "proteus"));
		}
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		// /* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		// Map<String, String> properties = new HashMap<String, String>();
		// properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
		// "user:pass@host.net:2212");
		// resourceDescriptor.setProperties(properties);

		ResourceIdentifier identifier = new ResourceIdentifier(resourceType);
		resourceDescriptor.setId(identifier.getId());

		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(resourceType);
		information.setName(resourceName);
		resourceDescriptor.setInformation(information);

		return resourceDescriptor;
	}

	public static CapabilityDescriptor newCapabilityDescriptor(String type, String model) {

		// TODO IS THERE A BETTER METHOD TO PASS THE URI?
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("")) {
			log.info("INFO: Getting uri param from terminal");
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		return newCapabilityDescriptor(type, model, "10.10", "netconf", uri);
	}

	public static CapabilityDescriptor newCapabilityDescriptorProteus(String type, String model) {

		// TODO IS THERE A BETTER METHOD TO PASS THE URI?
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("")) {
			log.info("INFO: Getting uri param from terminal");
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		return newCapabilityDescriptor(type, model, "1.0", "wonesys", uri);
	}

	public static CapabilityDescriptor newCapabilityDescriptor(String type, String model, String version, String protocol, String protocolUri) {

		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.PROTOCOL_URI, protocolUri);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, model);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_VERSION, version);
		capabilityDescriptor.getCapabilityProperties().add(property);

		property = new CapabilityProperty(ProtocolSessionContext.PROTOCOL,
				protocol);
		capabilityDescriptor.getCapabilityProperties().add(property);

		Information capabilityInformation = new Information();
		capabilityInformation.setType(type);
		capabilityDescriptor.setCapabilityInformation(capabilityInformation);

		return capabilityDescriptor;

	}

	public static ProfileDescriptor newProfileDescriptor(String profileName, String resourceType) {

		ProfileDescriptor profileDescriptor = new ProfileDescriptor();
		profileDescriptor.setProfileName(profileName);
		profileDescriptor.setResourceType(resourceType);

		return profileDescriptor;

	}

}
