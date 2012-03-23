package org.opennaas.extensions.router.model.mappers;

import java.util.ArrayList;
import java.util.List;

import jline.internal.Log;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.router.model.utils.ModelHelper;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.layer.Layer;
import org.opennaas.extensions.network.model.technology.ethernet.EthernetLayer;
import org.opennaas.extensions.network.model.technology.ethernet.TaggedEthernetLayer;
import org.opennaas.extensions.network.model.technology.ip.IPLayer;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkElement;

import org.opennaas.core.resources.IModel;

public class Cim2NdlMapper {

	/**
	 * Transforms given model to a NDL representation and adds it to networkModel.
	 * 
	 * @param model
	 * @param networkModel
	 * @param name
	 *            name for the model to add
	 * @return NetworkElements created to represent given model in networkModel.
	 */
	public static List<NetworkElement> addModelToNetworkModel(IModel model, NetworkModel networkModel, String name) {
		if (model instanceof System) {
			return addManagedElementToNetworkModel((System) model, networkModel, name);
		}
		return new ArrayList<NetworkElement>();
	}

	/**
	 * Transforms given managedElement to a NDL representation and adds it to networkModel.
	 * 
	 * @param managedElement
	 * @param networkModel
	 * @param name
	 *            name for the System to add
	 * @return NetworkElements created to represent given managedElement in networkModel.
	 */
	private static List<NetworkElement> addManagedElementToNetworkModel(System managedElement, NetworkModel networkModel, String name) {

		List<NetworkElement> createdElements = new ArrayList<NetworkElement>();

		// create device
		Device dev = addDeviceToNetworkModel(managedElement, networkModel, name);

		// create interfaces
		List<Interface> interfaces = addInterfacesToNetworkModel(managedElement, dev, networkModel);

		// create links
		List<Link> links = addLTLinksToNetworkModel(managedElement, interfaces, networkModel, dev);

		createdElements.add(dev);
		createdElements.addAll(interfaces);
		createdElements.addAll(links);

		return createdElements;
	}

	/**
	 * Creates a Device representing given ManagedElement and adds it to networkModel. Notice that a single Device is not a complete representation of
	 * a ManagedElement.
	 * 
	 * @param managedElement
	 * @param networkModel
	 * @param name
	 *            Name of the device
	 * @return Created Device.
	 */
	private static Device addDeviceToNetworkModel(System managedElement, NetworkModel networkModel, String name) {
		Device dev = new Device();
		if (name != null) {
			dev.setName(name);
		} else {
			dev.setName(managedElement.getName());
		}
		networkModel.getNetworkElements().add(dev);

		return dev;
	}

	/* method to add devices */
	private static String addResourceName(Device device, String name) {

		return device.getName() + ":" + name;
	}

	// /**
	// * FIXME change the interface place
	// */
	// private static boolean existInterface(List<Interface> interfaces, String name) {
	//
	// for (Interface interf : interfaces) {
	// if (interf.getName().equals(name))
	// return true;
	// }
	// return false;
	// }

	/**
	 * Creates interfaces representing given managedElement endpoints, and adds them to networkModel.
	 * 
	 * @param managedElement
	 * @param dev
	 * @param networkModel
	 * @return Created interfaces.
	 */
	private static List<Interface> addInterfacesToNetworkModel(System managedElement, Device dev, NetworkModel networkModel) {

		List<Interface> interfaces = new ArrayList<Interface>();

		try {
			for (NetworkPort port : ModelHelper.getInterfaces(managedElement)) {
				Interface topIface = generateInterfaceBasedOnPortType(port, dev, networkModel);

				// if (!existInterface(interfaces, topIface.getName()))
				interfaces.add(topIface);

				if (port.getProtocolEndpoint() != null) {

					// add VLAN interfaces
					for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
						if (endpoint instanceof VLANEndpoint) {
							// eth over eth
							Interface iface = new Interface();
							iface.setName(addResourceName(dev, port.getName() + "." + port.getPortNumber()));

							Layer ifaceLayer = obtainTaggedEthernetLayer(networkModel);
							iface.setLayer(ifaceLayer);

							iface.setServerInterface(topIface);
							topIface.getClientInterfaces().add(iface);

							// TODO is there other info to add? (e.g: VLANID?)

							iface.setDevice(dev);
							dev.getInterfaces().add(iface);

							networkModel.getNetworkElements().add(iface);
							interfaces.add(iface);
						}
					}

					// add IP interfaces
					for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
						if (endpoint instanceof IPProtocolEndpoint) {
							// ip over
							Interface iface = new Interface();
							// FIXME what if there is no ipv4 address?
							iface.setName(addResourceName(dev, ((IPProtocolEndpoint) endpoint).getIPv4Address()));

							Layer ifaceLayer = obtainIPLayer(networkModel);
							iface.setLayer(ifaceLayer);

							// find server interface
							// traverse interfaces in reverse order to find VLAN interfaces before pure eth ifaces
							Interface serverInterface = null;
							for (int i = interfaces.size() - 1; i >= 0 && serverInterface == null; i--) {
								if (interfaces.get(i).getName().equals(addResourceName(dev, port.getName() + "." + port.getPortNumber()))) {
									serverInterface = interfaces.get(i);
								}
							}
							iface.setServerInterface(serverInterface);
							serverInterface.getClientInterfaces().add(iface);

							// TODO is there other info to add? (e.g: ipAddress?)

							iface.setDevice(dev);
							dev.getInterfaces().add(iface);

							networkModel.getNetworkElements().add(iface);
							interfaces.add(iface);
						}
					}

					// TODO ADD OTHER PROTOCOL ENDPOINTS IN CORRECT ORDER (lower layer first)

				}
			}

		} catch (Exception e) {
			Log.debug(e.getMessage(), e.getCause());
			Log.debug("debugging");
		}

		return interfaces;
	}

	private static Interface generateInterfaceBasedOnPortType(NetworkPort port, Device dev, NetworkModel networkModel) {

		Interface iface = null;
		if (port instanceof EthernetPort || port instanceof LogicalTunnelPort) {

			iface = new Interface();
			iface.setName(addResourceName(dev, port.getName() + "." + port.getPortNumber()));
			// iface.setName(addResourceName(dev, port.getName()));

			Layer ethLayer = obtainEthernetLayer(networkModel);
			iface.setLayer(ethLayer);

			iface.setDevice(dev);
			dev.getInterfaces().add(iface);

			networkModel.getNetworkElements().add(iface);

		} else {
			// FIXME complete with other types of port
		}
		return iface;
	}

	/**
	 * Creates links representing LT connections in managedElement, and adds them to networkModel.
	 * 
	 * @param managedElement
	 * @param interfaces
	 * @param networkModel
	 * @return Created links.
	 */
	private static List<Link> addLTLinksToNetworkModel(System managedElement, List<Interface> interfaces, NetworkModel networkModel, Device dev) {

		List<LogicalTunnelPort> linkedPorts = new ArrayList<LogicalTunnelPort>();
		List<Link> links = new ArrayList<Link>();

		for (NetworkPort port : ModelHelper.getInterfaces((managedElement))) {
			if (port instanceof LogicalTunnelPort) {
				// avoid duplicating links
				if (!linkedPorts.contains((LogicalTunnelPort) port)) {

					// get lt endpoints
					LogicalTunnelPort srcPort = (LogicalTunnelPort) port;
					LogicalTunnelPort dstPort = null;
					long dstPortUnit = srcPort.getPeer_unit();
					for (NetworkPort otherPort : ModelHelper.getInterfaces(managedElement)) {
						if (otherPort instanceof LogicalTunnelPort) {
							if (otherPort.getPortNumber() == (int) dstPortUnit) {
								dstPort = (LogicalTunnelPort) otherPort;
								break;
							}
						}
					}

					if (dstPort != null) {
						// get interfaces matching lt endpoints
						Interface srcIface = null;
						Interface dstIface = null;
						for (Interface anIface : interfaces) {
							if (anIface.getName().equals(addResourceName(dev, srcPort.getName() + "." + srcPort.getPortNumber()))) {
								srcIface = anIface;

								// get interface connected to srcIface
								for (Interface otherIface : interfaces) {
									if (otherIface.getName().equals(addResourceName(dev, dstPort.getName() + "." + dstPort.getPortNumber()))) {
										if (otherIface.getLayer().equals(srcIface.getLayer())) {
											dstIface = otherIface;
											break;
										}
									}
								}
								if (dstIface != null) {
									Link link = NetworkModelHelper.linkInterfaces(srcIface, dstIface, true);
									networkModel.getNetworkElements().add(link);

									links.add(link);

									// avoid duplicating links
									linkedPorts.add(srcPort);
									linkedPorts.add(dstPort);
								}
							}
						}
					}
				}
			}
		}
		return links;
	}

	private static Layer obtainEthernetLayer(NetworkModel networkModel) {
		for (Layer layer : NetworkModelHelper.getLayers(networkModel)) {
			if (layer instanceof EthernetLayer) {
				return layer;
			}
		}
		return new EthernetLayer();
	}

	private static Layer obtainIPLayer(NetworkModel networkModel) {
		for (Layer layer : NetworkModelHelper.getLayers(networkModel)) {
			if (layer instanceof IPLayer) {
				return layer;
			}
		}
		return new IPLayer();
	}

	private static Layer obtainTaggedEthernetLayer(NetworkModel networkModel) {
		for (Layer layer : NetworkModelHelper.getLayers(networkModel)) {
			if (layer instanceof TaggedEthernetLayer) {
				return layer;
			}
		}
		return new TaggedEthernetLayer();
	}
}
