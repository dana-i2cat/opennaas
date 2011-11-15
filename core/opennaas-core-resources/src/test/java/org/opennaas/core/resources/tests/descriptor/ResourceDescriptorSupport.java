package org.opennaas.core.resources.tests.descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.NetworkInfo;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceId;


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
	
	/**
	 * Create a resource with properties 
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
		
		/* network description */
		NetworkInfo networkInfo = new NetworkInfo();
		List<ResourceId> resources = new ArrayList<ResourceId>();

		ResourceId resourceId = new ResourceId();
		resourceId.setName("logical1");
		resourceId.setType("router");

		resources.add(resourceId);

		ResourceId resourceId2 = new ResourceId();
		
		resourceId2.setName("logical2");
		resourceId2.setType("router");
		resources.add(resourceId2);
		
		networkInfo.setResources(resources);
		config.setNetworkInfo(networkInfo);
		
		
		
		return config;
	}
	
	
	/**
	 * Create network descriptor
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

	
		Map<String, String> props = new HashMap<String,String>();
		props.put("virtual","true");
		config.setProperties(props);
		
		return config;
	}


	
	
}
