package org.opennaas.extensions.vcpe.descriptor;

import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.extensions.vcpe.descriptor.request.VCPENetworkRequest;

public class VCPENetworkDescriptor extends ResourceDescriptor {

	private VCPENetworkRequest	request;

	public VCPENetworkRequest getRequest() {
		return request;
	}

	public void setRequest(VCPENetworkRequest request) {
		this.request = request;
	}

}
