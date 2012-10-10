package org.opennaas.core.resources.descriptor.vcpe.helper;

import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;

public class VCPENetworkDescriptorHelper {

	public static VCPENetworkDescriptor generateSampleDescriptor(String name, String model) {

		VCPENetworkDescriptor descriptor = new VCPENetworkDescriptor();

		Information info = new Information();
		info.setType("vcpenet");
		info.setName(name);
		descriptor.setInformation(info);

		if (model != null)
			descriptor.setvCPEModel(model);

		return descriptor;
	}
}
