package org.opennaas.core.resources.descriptor.vcpe.helper;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.core.resources.descriptor.vcpe.request.RequestDevice;
import org.opennaas.core.resources.descriptor.vcpe.request.RequestDomain;
import org.opennaas.core.resources.descriptor.vcpe.request.RequestElement;
import org.opennaas.core.resources.descriptor.vcpe.request.RequestInterface;
import org.opennaas.core.resources.descriptor.vcpe.request.RequestLink;
import org.opennaas.core.resources.descriptor.vcpe.request.VCPENetworkRequest;

public class VCPENetworkDescriptorHelper {

	public static VCPENetworkDescriptor generateSampleDescriptor(String name, VCPENetworkRequest request) {

		VCPENetworkDescriptor descriptor = new VCPENetworkDescriptor();

		Information info = new Information();
		info.setType("vcpenet");
		info.setName(name);
		descriptor.setInformation(info);

		if (request != null)
			descriptor.setRequest(request);

		return descriptor;
	}

	public static VCPENetworkRequest generateSampleRequest() {

		// create entities
		RequestDomain dom1 = new RequestDomain();
		RequestDevice dev1 = new RequestDevice();
		RequestDevice dev2 = new RequestDevice();
		RequestDevice dev3 = new RequestDevice();

		RequestInterface iface11 = new RequestInterface();
		RequestInterface iface12 = new RequestInterface();
		RequestInterface iface21 = new RequestInterface();
		RequestInterface iface22 = new RequestInterface();
		RequestInterface iface31 = new RequestInterface();
		RequestInterface iface32 = new RequestInterface();

		RequestLink link1 = new RequestLink();
		RequestLink link2 = new RequestLink();

		dom1.setId("dom1");
		dev1.setId("dev1");
		dev2.setId("dev2");
		dev3.setId("dev3");
		iface11.setId("iface1");
		iface12.setId("iface2");
		iface21.setId("iface3");
		iface22.setId("iface4");
		iface31.setId("iface5");
		iface32.setId("iface6");
		link1.setId("link1");
		link2.setId("link2");

		List<RequestElement> elems = new ArrayList<RequestElement>();
		elems.add(dom1);
		elems.add(dev1);
		elems.add(dev2);
		elems.add(dev3);
		elems.add(iface11);
		elems.add(iface12);
		elems.add(iface21);
		elems.add(iface22);
		elems.add(iface31);
		elems.add(iface32);
		elems.add(link1);
		elems.add(link2);

		// add relationships

		List<RequestDevice> devs = new ArrayList<RequestDevice>();
		devs.add(dev3);
		dom1.setDevices(devs);

		List<RequestInterface> ifaces = new ArrayList<RequestInterface>();
		ifaces.add(iface11);
		ifaces.add(iface12);
		dom1.setInterfaces(ifaces);

		ifaces = new ArrayList<RequestInterface>();
		ifaces.add(iface21);
		ifaces.add(iface31);
		dev1.setInterfaces(ifaces);

		ifaces = new ArrayList<RequestInterface>();
		ifaces.add(iface22);
		ifaces.add(iface32);
		dev2.setInterfaces(ifaces);

		link(link1, iface11, iface21);
		link(link2, iface12, iface22);

		VCPENetworkRequest req = new VCPENetworkRequest();
		req.setElements(elems);

		return req;
	}

	public static void link(RequestLink link, RequestInterface source, RequestInterface sink) {
		link.setSource(source);
		link.setSink(sink);

		source.setLinkTo(link);
		sink.setLinkTo(link);
	}

}
