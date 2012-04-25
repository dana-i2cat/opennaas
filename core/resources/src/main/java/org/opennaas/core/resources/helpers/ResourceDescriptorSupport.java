package org.opennaas.core.resources.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

public class ResourceDescriptorSupport {

	public static ResourceDescriptor createSampleDescriptor() {
		ResourceDescriptor config = new ResourceDescriptor();
		config.setId(new String("1"));
		Information info = new Information();
		info.setDescription("Test");
		info.setType("ca.inocybe.xxx");
		info.setName("Resource");
		info.setVersion("1.0.0");
		config.setInformation(info);
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();
		properties.add(new CapabilityProperty("name", "value"));
		capabilityDescriptor.setCapabilityProperties(properties);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(capabilityDescriptor);
		config.setCapabilityDescriptors(capabilityDescriptors);
		return config;
	}

	/**
	 * Create network descriptor
	 * 
	 * @return
	 */
	public static ResourceDescriptor createVirtualResourceDescriptor() {
		ResourceDescriptor config = new ResourceDescriptor();
		config.setId(new String("1"));
		Information info = new Information();
		info.setDescription("virtual resource description");
		info.setType("router");
		info.setName("logical1");
		info.setVersion("1.0.0");
		config.setInformation(info);

		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();
		properties.add(new CapabilityProperty("name", "value"));
		capabilityDescriptor.setCapabilityProperties(properties);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(capabilityDescriptor);
		config.setCapabilityDescriptors(capabilityDescriptors);

		Map<String, String> props = new HashMap<String, String>();
		props.put("virtual", "true");
		config.setProperties(props);

		return config;
	}

}
