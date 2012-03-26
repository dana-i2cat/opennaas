package net.i2cat.mantychore.network.repository;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.domain.NetworkDomain;
import net.i2cat.mantychore.network.model.topology.ConnectionPoint;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.Interface;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

import org.opennaas.core.resources.descriptor.network.DeviceId;
import org.opennaas.core.resources.descriptor.network.InterfaceId;
import org.opennaas.core.resources.descriptor.network.Layer;
import org.opennaas.core.resources.descriptor.network.LayerId;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;

public class NetworkMapperModelToDescriptor {

	public static NetworkTopology modelToDescriptor(NetworkModel networkModel) {
		List<org.opennaas.core.resources.descriptor.network.Device> existingDevices = new ArrayList<org.opennaas.core.resources.descriptor.network.Device>();
		List<org.opennaas.core.resources.descriptor.network.NetworkDomain> existingDomains = new ArrayList<org.opennaas.core.resources.descriptor.network.NetworkDomain>();
		List<org.opennaas.core.resources.descriptor.network.Interface> existingInterfaces = new ArrayList<org.opennaas.core.resources.descriptor.network.Interface>();
		// List<org.opennaas.core.resources.descriptor.network.Link> existingLinks = new
		// ArrayList<org.opennaas.core.resources.descriptor.network.Link>();

		/* added all elements */
		for (NetworkElement networkElement : networkModel.getNetworkElements()) {
			if (networkElement instanceof Device) {
				existingDevices.add(newDevice((Device) networkElement));
			} else if (networkElement instanceof Interface) {
				existingInterfaces.add(newInterface((Interface) networkElement));
			} else if (networkElement instanceof NetworkDomain) {
				existingDomains.add(newNetworkDomain((NetworkDomain) networkElement));
			}

		}

		/* we create all the necessary associations */

		/* using domains */
		for (NetworkDomain networkDomain : NetworkModelHelper.getDomains(networkModel.getNetworkElements())) {
			int posNetworkDomain = getNetworkDomain(networkDomain.getName(), existingDomains);
			org.opennaas.core.resources.descriptor.network.NetworkDomain networkDomainDescriptor = existingDomains.get(posNetworkDomain);
			List<DeviceId> deviceIds = new ArrayList<DeviceId>();
			for (Device device : networkDomain.getHasDevice()) {
				deviceIds.add(newDeviceId(device.getName()));
			}

			networkDomainDescriptor.setHasDevices(deviceIds);
		}

		/* using device */
		for (Device device : NetworkModelHelper.getDevices(networkModel.getNetworkElements())) {
			int posDevice = getDevice(device.getName(), existingDevices);
			org.opennaas.core.resources.descriptor.network.Device deviceDescriptor = existingDevices.get(posDevice);

			List<InterfaceId> interfaceIds = new ArrayList<InterfaceId>();
			for (ConnectionPoint interf : device.getInterfaces()) {
				interfaceIds.add(newInterfaceId(interf.getName()));
			}

			deviceDescriptor.setHasInterfaces(interfaceIds);
		}

		/* using interface */
		for (Interface interf : NetworkModelHelper.getInterfaces(networkModel.getNetworkElements())) {
			int posInterface = getInterface(interf.getName(), existingInterfaces);
			org.opennaas.core.resources.descriptor.network.Interface interfaceDescriptor = existingInterfaces.get(posInterface);
			if (interf.getLinkTo() != null) {
				String toLinkName = "";
				if (interf.getLinkTo().getSource().getName().equals(interf.getName())) {
					toLinkName = interf.getLinkTo().getSink().getName();
				} else {
					toLinkName = interf.getLinkTo().getSource().getName();
				}
				interfaceDescriptor.setLinkTo(newLink(toLinkName));
			}

		}

		NetworkTopology networkTopology = new NetworkTopology();
		networkTopology.setDevices(existingDevices);
		networkTopology.setInterfaces(existingInterfaces);
		networkTopology.setNetworkDomains(existingDomains);
		networkTopology.setLayers(getLayers(networkModel));

		return networkTopology;
	}

	private static String addNumberSign(String name) {
		if (!name.startsWith("#"))
			return "#" + name;
		else
			return name;
	}

	private static List<Layer> getLayers(NetworkModel networkModel) {
		List<Layer> layers = new ArrayList<Layer>();
		for (net.i2cat.mantychore.network.model.layer.Layer layer : NetworkModelHelper.getLayers(networkModel.getNetworkElements())) {
			Layer descriptorLayer = new Layer();
			descriptorLayer.setName(layer.getName());
			layers.add(descriptorLayer);
		}
		return layers;
	}

	private static int getNetworkDomain(String name, List<org.opennaas.core.resources.descriptor.network.NetworkDomain> existingDomains) {
		int pos = 0;
		for (org.opennaas.core.resources.descriptor.network.NetworkDomain networkDomain : existingDomains) {
			if (networkDomain.getName().equals(name))
				return pos;
			pos++;
		}
		return -1;
	}

	private static DeviceId newDeviceId(String name) {
		DeviceId deviceId = new DeviceId();
		deviceId.setResource(name);
		return deviceId;
	}

	private static int getDevice(String name, List<org.opennaas.core.resources.descriptor.network.Device> existingDevices) {
		int pos = 0;
		for (org.opennaas.core.resources.descriptor.network.Device device : existingDevices) {
			if (device.getName().equals(name))
				return pos;
			pos++;
		}

		return -1;
	}

	private static InterfaceId newInterfaceId(String name) {
		InterfaceId interfaceId = new InterfaceId();
		interfaceId.setResource(name);
		return interfaceId;
	}

	private static org.opennaas.core.resources.descriptor.network.NetworkDomain newNetworkDomain(NetworkDomain networkElement) {
		org.opennaas.core.resources.descriptor.network.NetworkDomain networkDomain = new org.opennaas.core.resources.descriptor.network.NetworkDomain();
		networkDomain.setName(networkElement.getName());
		return networkDomain;
	}

	private static org.opennaas.core.resources.descriptor.network.Link newLink(String name) {
		org.opennaas.core.resources.descriptor.network.Link link = new org.opennaas.core.resources.descriptor.network.Link();
		link.setName(name);
		return link;
	}

	private static org.opennaas.core.resources.descriptor.network.Interface newInterface(Interface networkElement) {
		org.opennaas.core.resources.descriptor.network.Interface interf = new org.opennaas.core.resources.descriptor.network.Interface();
		LayerId layerId = new LayerId();
		layerId.setResource(networkElement.getLayer().getName());
		interf.setName(networkElement.getName());
		interf.setAtLayer(layerId);
		return interf;
	}

	private static org.opennaas.core.resources.descriptor.network.Device newDevice(Device networkElement) {
		org.opennaas.core.resources.descriptor.network.Device device = new org.opennaas.core.resources.descriptor.network.Device();
		device.setName(networkElement.getName());
		return device;
	}

	private static int getInterface(String name, List<org.opennaas.core.resources.descriptor.network.Interface> interfaces) {
		int pos = 0;
		for (org.opennaas.core.resources.descriptor.network.Interface interf : interfaces) {
			if (interf.getName().equals(name)) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

}
