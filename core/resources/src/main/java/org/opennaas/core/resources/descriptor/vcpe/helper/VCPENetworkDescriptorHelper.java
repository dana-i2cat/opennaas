package org.opennaas.core.resources.descriptor.vcpe.helper;

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
import java.util.List;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;

public class VCPENetworkDescriptorHelper {

	public static VCPENetworkDescriptor generateSampleDescriptor(String name, String model) {

		VCPENetworkDescriptor descriptor = new VCPENetworkDescriptor();

		Information info = new Information();
		info.setType(VCPENetworkDescriptor.RESOURCE_TYPE);
		info.setName(name);
		descriptor.setInformation(info);

		List<CapabilityDescriptor> capabs = new ArrayList<CapabilityDescriptor>();
		capabs.add(generateBuilderCapabilityDescriptor());
		descriptor.setCapabilityDescriptors(capabs);

		if (model != null)
			descriptor.setvCPEModel(model);

		return descriptor;
	}

	public static CapabilityDescriptor generateBuilderCapabilityDescriptor() {
		CapabilityDescriptor desc = new CapabilityDescriptor();
		Information info = new Information();
		info.setType("vcpenet_builder");
		desc.setCapabilityInformation(info);
		return desc;
	}
}
