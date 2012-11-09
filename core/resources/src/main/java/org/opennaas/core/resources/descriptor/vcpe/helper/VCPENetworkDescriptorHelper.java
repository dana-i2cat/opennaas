package org.opennaas.core.resources.descriptor.vcpe.helper;

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
