package net.i2cat.nexus.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//TODO TO FIX!! WE HAVE TO KNOW HOW A RESOURCE SHOULD BE FILLED 
public class CapabilityHelper {
	/**
	 * Create a new resource descriptor of an arbitrary type.
	 * @param type type of resource descriptor.
	 * @return a descriptor
	 */
	public static ResourceDescriptor newResourceDescriptor(String type) {
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
	 * Create a Capability Descriptor of an arbitrary type but proteus/1.0. 
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
}