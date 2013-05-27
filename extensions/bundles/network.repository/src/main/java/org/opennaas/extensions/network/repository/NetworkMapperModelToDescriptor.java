package org.opennaas.extensions.network.repository;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.descriptor.network.DeviceId;
import org.opennaas.core.resources.descriptor.network.InterfaceId;
import org.opennaas.core.resources.descriptor.network.Layer;
import org.opennaas.core.resources.descriptor.network.LayerId;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.network.model.topology.ConnectionPoint;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;

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
					toLinkName = newInterfaceId(interf.getLinkTo().getSink().getName()).getResource();
				} else {
					toLinkName = newInterfaceId(interf.getLinkTo().getSource().getName()).getResource();
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

	private static String addHashCharacter(String name) {
		if (!name.startsWith("#"))
			return "#" + name;
		else
			return name;
	}

	private static List<Layer> getLayers(NetworkModel networkModel) {
		List<Layer> layers = new ArrayList<Layer>();
		for (org.opennaas.extensions.network.model.layer.Layer layer : NetworkModelHelper.getLayers(networkModel.getNetworkElements())) {
			Layer descriptorLayer = new Layer();
			descriptorLayer.setName(layer != null ? addHashCharacter(layer.getName()) : null);
			layers.add(descriptorLayer);
		}
		return layers;
	}

	/**
	 * 
	 * @param name
	 *            Name of the NetworkDomain in network model to get
	 * @param existingDomains
	 *            List of NetworkDomains in descriptor topology.
	 * @return Position inside existingDomains of the NetworkDomain to get, or -1 if existingDomains does not contain desired domain.
	 */
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
		deviceId.setResource(addHashCharacter(name));
		return deviceId;
	}

	/**
	 * 
	 * @param name
	 *            Name of the Device in network model to get
	 * @param existingDevices
	 *            List of Interfaces in descriptor topology.
	 * @return Position inside existingDevices of the Device to get, or -1 if existingDevices does not contain desired device.
	 */
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
		interfaceId.setResource(addHashCharacter(name));
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
		layerId.setResource(networkElement.getLayer() != null ? networkElement.getLayer().getName() : null);
		interf.setName(networkElement.getName());
		interf.setAtLayer(layerId);
		return interf;
	}

	private static org.opennaas.core.resources.descriptor.network.Device newDevice(Device networkElement) {
		org.opennaas.core.resources.descriptor.network.Device device = new org.opennaas.core.resources.descriptor.network.Device();
		device.setName(networkElement.getName());
		return device;
	}

	/**
	 * 
	 * @param name
	 *            Name of the Interface in network model to get
	 * @param interfaces
	 *            List of Interfaces in descriptor topology.
	 * @return Position inside interfaces of the Interface to get, or -1 if interfaces does not contain desired interface.
	 */
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
