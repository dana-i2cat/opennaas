package org.opennaas.core.resources.descriptor.vcpe;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.vcpe.request.VCPENetworkRequest;

@XmlRootElement
public class VCPENetworkDescriptor extends ResourceDescriptor {

	private VCPENetworkRequest	request;

	public VCPENetworkRequest getRequest() {
		return request;
	}

	public void setRequest(VCPENetworkRequest request) {
		this.request = request;
	}

}
