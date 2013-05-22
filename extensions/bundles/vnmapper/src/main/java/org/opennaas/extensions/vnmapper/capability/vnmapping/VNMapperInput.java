package org.opennaas.extensions.vnmapper.capability.vnmapping;

import org.opennaas.extensions.vnmapper.InPNetwork;
import org.opennaas.extensions.vnmapper.VNTRequest;

public class VNMapperInput {

	private InPNetwork	net;
	private VNTRequest	request;

	public VNMapperInput(InPNetwork net, VNTRequest request) {
		this.net = net;
		this.request = request;
	}

	public InPNetwork getNet() {
		return net;
	}

	public void setNet(InPNetwork net) {
		this.net = net;
	}

	public VNTRequest getRequest() {
		return request;
	}

	public void setRequest(VNTRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return net.toString() + "\n" + request.toString() + "\n";
	}

}
