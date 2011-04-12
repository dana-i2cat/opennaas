package net.i2cat.nexus.resources.tests.descriptor;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.CapabilityProperty;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

import junit.framework.TestCase;

public class ResourceDescriptorSupport extends TestCase
{
	protected ResourceDescriptor createSampleDescriptor() {
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
}
