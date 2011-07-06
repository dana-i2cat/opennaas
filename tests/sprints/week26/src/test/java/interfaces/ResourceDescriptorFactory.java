package interfaces;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.nexus.resources.ResourceIdentifier;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.CapabilityProperty;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
			capabilityDescriptors.add(newCapabilityDescriptor(capability, type));
		}

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		// /* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		// properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
		// "user:pass@host.net:2212");

		resourceDescriptor.setProperties(properties);
		ResourceIdentifier identifier = new ResourceIdentifier(type);
		resourceDescriptor.setId(identifier.getId());
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(name);
		information.setName("Switch Test");
		resourceDescriptor.setInformation(information);

		return resourceDescriptor;
	}

	public static ResourceDescriptor newResourceDescriptorProteus(String name, String type, List<String> capabilities) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		capabilityDescriptors.add(newCapabilityDescriptor("connections", "proteus"));
		capabilityDescriptors.add(newCapabilityDescriptor("queue", "junos"));

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		// /* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		// properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
		// "user:pass@host.net:2212");

		resourceDescriptor.setProperties(properties);
		ResourceIdentifier identifier = new ResourceIdentifier(type);
		resourceDescriptor.setId(identifier.getId());
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(name);
		information.setName("Switch Test");
		resourceDescriptor.setInformation(information);

		return resourceDescriptor;
	}

	public static CapabilityDescriptor newCapabilityDescriptor(String type, String model) {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		CapabilityProperty property = new CapabilityProperty(
				ResourceDescriptorConstants.ACTION_NAME, model);
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
