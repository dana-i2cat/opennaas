package org.opennaas.extensions.network.mock;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.network.model.topology.ConnectionPoint;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkElement;

public class MockNetworkModel {

	public static NetworkModel newNetworkModel() {
		NetworkModel networkModel = new NetworkModel();
		List<NetworkElement> networkElements = new ArrayList<NetworkElement>();
		createInterfaces(networkElements);
		createDevices(networkElements);
		createNetworkDomains(networkElements);
		createLinks(networkElements);

		networkModel.setNetworkElements(networkElements);
		return networkModel;

	}

	public static List<NetworkElement> createNetworkDomains(List<NetworkElement> networkElements) {

		List<NetworkElement> listDomains = new ArrayList<NetworkElement>();
		NetworkDomain networkDomain = new NetworkDomain();
		networkDomain.setName("AS1");
		List<Device> hasDevice = new ArrayList<Device>();
		hasDevice.add((Device) networkElements.get(9));
		hasDevice.add((Device) networkElements.get(10));
		hasDevice.add((Device) networkElements.get(11));
		networkDomain.setHasDevice(hasDevice);
		listDomains.add(networkDomain);
		networkElements.addAll(listDomains);
		return listDomains;

	}

	public static List<NetworkElement> createDevices(List<NetworkElement> networkElements) {
		List<NetworkElement> listDevices = new ArrayList<NetworkElement>();

		Device device = createDevice("router:R-AS2-1");
		List<ConnectionPoint> interfaces = new ArrayList<ConnectionPoint>();
		interfaces.add((ConnectionPoint) networkElements.get(0));
		interfaces.add((ConnectionPoint) networkElements.get(1));
		interfaces.add((ConnectionPoint) networkElements.get(2));
		device.setInterfaces(interfaces);
		listDevices.add(device);

		Device device2 = createDevice("router:R-AS2-2");
		List<ConnectionPoint> interfaces2 = new ArrayList<ConnectionPoint>();
		interfaces2.add((ConnectionPoint) networkElements.get(3));
		interfaces2.add((ConnectionPoint) networkElements.get(4));
		interfaces2.add((ConnectionPoint) networkElements.get(5));
		device2.setInterfaces(interfaces2);
		listDevices.add(device2);

		Device device3 = createDevice("router:R-AS2-3");
		List<ConnectionPoint> interfaces3 = new ArrayList<ConnectionPoint>();
		interfaces3.add((ConnectionPoint) networkElements.get(6));
		interfaces3.add((ConnectionPoint) networkElements.get(7));
		device3.setInterfaces(interfaces3);
		listDevices.add(device3);

		networkElements.addAll(listDevices);
		return networkElements;

	}

	public static List<NetworkElement> createInterfaces(List<NetworkElement> networkElements) {
		List<NetworkElement> listInterfaces = new ArrayList<NetworkElement>();
		listInterfaces.add(createInterface("router:R-AS2-1:lt-1/2/0.51"));
		listInterfaces.add(createInterface("router:R-AS2-1:lt-1/2/0.100"));
		listInterfaces.add(createInterface("router:R-AS2-1:lo0.1"));
		listInterfaces.add(createInterface("router:R-AS2-2:lt-1/2/0.102"));
		listInterfaces.add(createInterface("router:R-AS2-2:lt-1/2/0.101"));
		listInterfaces.add(createInterface("router:R-AS2-2:lo0.3"));
		listInterfaces.add(createInterface("router:R-AS2-3:lt-1/2/0.103"));
		listInterfaces.add(createInterface("router:R-AS2-3:lo0.4"));

		/* external network */
		listInterfaces.add(createInterface("router:R1:lt-1/2/0.50"));

		networkElements.addAll(listInterfaces);
		return networkElements;
	}

	public static List<NetworkElement> createLinks(List<NetworkElement> networkElements) {
		List<NetworkElement> listLinks = new ArrayList<NetworkElement>();

		/* link interfs 51-exterior */
		Link link = new Link();
		link.setSource((Interface) networkElements.get(0));
		link.setSink((Interface) networkElements.get(8));
		link.setBidirectional(false);

		((Interface) networkElements.get(0)).setLinkTo(link);
		listLinks.add(link);

		/* link interfs 100-101 */
		Link link2 = new Link();
		link2.setSource((Interface) networkElements.get(1));
		link2.setSink((Interface) networkElements.get(4));
		link2.setBidirectional(true);
		((Interface) networkElements.get(1)).setLinkTo(link2);
		((Interface) networkElements.get(4)).setLinkTo(link2);
		listLinks.add(link2);

		/* link interfs 102-103 */
		Link link3 = new Link();
		link3.setSource((Interface) networkElements.get(3));
		link3.setSink((Interface) networkElements.get(6));
		link3.setBidirectional(true);
		((Interface) networkElements.get(3)).setLinkTo(link3);
		((Interface) networkElements.get(6)).setLinkTo(link3);
		listLinks.add(link3);

		networkElements.addAll(listLinks);
		return networkElements;

	}

	public static Device createDevice(String name) {
		Device device = new Device();
		device.setName(name);
		return device;

	}

	private static NetworkElement createInterface(String name) {
		Interface interf = new Interface();
		interf.setName(name);
		return interf;
	}

	private static int getInterface(String name, List<ConnectionPoint> listInterfaces) {
		int pos = 0;
		for (ConnectionPoint connectionPoint : listInterfaces) {
			if (connectionPoint.getName().equals(name))
				return pos;
			pos++;
		}

		return -1;
	}

	private static int getDevice(String name, List<Device> devices) {
		int pos = 0;
		for (Device device : devices) {
			if (device.getName().equals(name))
				return pos;
			pos++;
		}
		return -1;
	}
}
