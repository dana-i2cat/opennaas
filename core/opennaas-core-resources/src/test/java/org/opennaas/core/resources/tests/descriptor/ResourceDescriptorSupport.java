package org.opennaas.core.resources.tests.descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockNetworkDescriptor;

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

	/**
	 * Create a resource with properties
	 * 
	 * @return
	 */
	protected ResourceDescriptor createNetworkDescriptor() {
		ResourceDescriptor config = new ResourceDescriptor();
		config.setId(new String("1"));
		/* resource description */
		Information info = new Information();
		info.setDescription("network description");
		info.setType("network");
		info.setName("networklayer1.0");
		info.setVersion("1.0.0");
		config.setInformation(info);

		/* capability description */
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();
		properties.add(new CapabilityProperty("name", "value"));
		capabilityDescriptor.setCapabilityProperties(properties);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(capabilityDescriptor);
		config.setCapabilityDescriptors(capabilityDescriptors);

		/* read from a file descriptor */
		config.setFileTopology("network/network_example1.xml");

		// Testing if it loads a network topology
		config.setNetworkTopology(MockNetworkDescriptor.newSimpleNDLNetworkDescriptor());

		return config;
	}

	/**
	 * Create a resource with properties
	 * 
	 * @return
	 */
	protected ResourceDescriptor createNetworkDescriptorWithNetworkDomains() {
		ResourceDescriptor config = new ResourceDescriptor();
		config.setId(new String("1"));

		/* resource description */
		Information info = new Information();
		info.setDescription("network description");
		info.setType("network");
		info.setName("networklayer1.0");
		info.setVersion("1.0.0");
		config.setInformation(info);

		/* capability description */
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		List<CapabilityProperty> properties = new ArrayList<CapabilityProperty>();
		properties.add(new CapabilityProperty("name", "value"));
		capabilityDescriptor.setCapabilityProperties(properties);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(capabilityDescriptor);
		config.setCapabilityDescriptors(capabilityDescriptors);

		/* read from a file descriptor */
		config.setFileTopology("network/network_diffs_layer.xml");

		// Testing if it loads a network topology
		config.setNetworkTopology(MockNetworkDescriptor.newNetworkDescriptorWithNetworkDomain());

		return config;
	}

	/**
	 * Create network descriptor
	 * 
	 * @return
	 */
	protected ResourceDescriptor createVirtualResourceDescriptor() {
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
