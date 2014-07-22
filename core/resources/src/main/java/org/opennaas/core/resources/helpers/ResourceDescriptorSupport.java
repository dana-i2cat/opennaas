package org.opennaas.core.resources.helpers;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
