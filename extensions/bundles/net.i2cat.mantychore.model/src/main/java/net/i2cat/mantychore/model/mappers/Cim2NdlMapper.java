package net.i2cat.mantychore.model.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.System;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.mantychore.model.utils.ModelHelper;
import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.layer.Layer;
import net.i2cat.mantychore.network.model.technology.ethernet.EthernetLayer;
import net.i2cat.mantychore.network.model.technology.ethernet.TaggedEthernetLayer;
import net.i2cat.mantychore.network.model.technology.ip.IPLayer;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.Interface;
import net.i2cat.mantychore.network.model.topology.Link;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

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

	/**
	 * Creates interfaces representing given managedElement endpoints, and adds them to networkModel. Add physical ethernet, pure ethernet, tagged
	 * ethernet and ip interfaces
	 * 
	 * @param managedElement
	 * @param dev
	 * @param networkModel
	 * @return Created interfaces.
	 */
	private static List<Interface> addInterfacesToNetworkModel(System managedElement, Device dev, NetworkModel networkModel) {
		List<Interface> interfaces = new ArrayList<Interface>();

		// Add the physical interfaces
		interfaces.addAll(addPhysicalInterfaces(managedElement, dev, networkModel));

		// Add the pure ethernet interfaces
		interfaces.addAll(addPureEthernetInterfaces(managedElement, dev, networkModel));

		// Add the tagged ethernet interfaces
		interfaces.addAll(addTaggedEthernetInterfaces(managedElement, dev, networkModel));

		// Add the ip interfaces
		interfaces.addAll(addIPInterfaces(managedElement, dev, networkModel));

		return interfaces;
	}

	/**
	 * Add all the physical interfaces to the model if it don't exist
	 * 
	 * @param managedElement
	 * @param dev
	 * @param networkModel
	 * @return list of interfaces
	 */
	private static Collection<? extends Interface> addPhysicalInterfaces(System managedElement, Device dev, NetworkModel networkModel) {
		List<Interface> listInterface = new ArrayList<Interface>();
		List<NetworkPort> listNetworkPort = ModelHelper.getInterfaces(managedElement);

		for (NetworkPort port : listNetworkPort) {
			if (port instanceof EthernetPort || port instanceof LogicalTunnelPort) {
				if (!existsdPhysicalInterface(listInterface, addResourceName(dev, port.getName()))) {
					Layer ethLayer = obtainEthernetLayer(networkModel);

					Interface iface = new Interface();
					iface.setName(addResourceName(dev, port.getName()));
					iface.setLayer(ethLayer);
					iface.setDevice(dev);

					dev.getInterfaces().add(iface);
					networkModel.getNetworkElements().add(iface);
					listInterface.add(iface);
				}
			}
		}
		return listInterface;
	}

	/**
	 * Add the pure ethernet interfaces in the model -> Haven't VLAN enpoints
	 * 
	 * @param managedElement
	 * @param dev
	 * @param networkModel
	 * @return list of interfaces
	 */
	private static Collection<? extends Interface> addPureEthernetInterfaces(System managedElement, Device dev, NetworkModel networkModel) {
		List<Interface> listInterface = new ArrayList<Interface>();
		List<NetworkPort> listNetworkPort = ModelHelper.getInterfaces(managedElement);

		for (NetworkPort port : listNetworkPort) {
			if (!haveVLANEndpoints(port)) {
				if (port instanceof EthernetPort || port instanceof LogicalTunnelPort) {
					Layer ethLayer = obtainEthernetLayer(networkModel);

					Interface iface = new Interface();
					iface.setName(addResourceName(dev, port.getName() + "." + port.getPortNumber()));
					iface.setLayer(ethLayer);
					iface.setDevice(dev);

					Interface topIFace = getInterfaceByName(networkModel.getNetworkElements(), addResourceName(dev, port.getName()));
					if (topIFace != null) {
						iface.setServerInterface(topIFace);
						topIFace.getClientInterfaces().add(iface);
					}

					dev.getInterfaces().add(iface);
					networkModel.getNetworkElements().add(iface);
					listInterface.add(iface);
				}
			}
		}
		return listInterface;
	}

	/**
	 * @param managedElement
	 * @param dev
	 * @param networkModel
	 * @return list of interfaces
	 */
	private static Collection<? extends Interface> addTaggedEthernetInterfaces(System managedElement, Device dev, NetworkModel networkModel) {
		List<Interface> listInterface = new ArrayList<Interface>();
		List<NetworkPort> listNetworkPort = ModelHelper.getInterfaces(managedElement);

		for (NetworkPort port : listNetworkPort) {
			if (port.getProtocolEndpoint() != null) {
				for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
					if (endpoint instanceof VLANEndpoint) {
						Layer ifaceLayer = obtainTaggedEthernetLayer(networkModel);

						Interface iface = new Interface();
						iface.setName(addResourceName(dev, port.getName() + "." + port.getPortNumber()));
						iface.setLayer(ifaceLayer);
						iface.setDevice(dev);

						Interface topIFace = getInterfaceByName(networkModel.getNetworkElements(), addResourceName(dev, port.getName()));
						if (topIFace != null) {
							iface.setServerInterface(topIFace);
							topIFace.getClientInterfaces().add(iface);
						}

						dev.getInterfaces().add(iface);
						networkModel.getNetworkElements().add(iface);
						listInterface.add(iface);
					}
				}
			}
		}
		return listInterface;
	}

	/**
	 * @param managedElement
	 * @param dev
	 * @param networkModel
	 * @return list of interfaces
	 */
	private static Collection<? extends Interface> addIPInterfaces(System managedElement, Device dev, NetworkModel networkModel) {
		List<Interface> listInterface = new ArrayList<Interface>();
		List<NetworkPort> listNetworkPort = ModelHelper.getInterfaces(managedElement);

		for (NetworkPort port : listNetworkPort) {
			if (port.getProtocolEndpoint() != null) {
				for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
					if (endpoint instanceof IPProtocolEndpoint) {
						Layer ifaceLayer = obtainIPLayer(networkModel);

						Interface iface = new Interface();
						iface.setName(addResourceName(dev, ((IPProtocolEndpoint) endpoint).getIPv4Address()));
						iface.setLayer(ifaceLayer);

						Interface topIFace = getInterfaceByName(networkModel.getNetworkElements(),
								addResourceName(dev, port.getName() + "." + port.getPortNumber()));
						if (topIFace != null) {
							iface.setServerInterface(topIFace);
							topIFace.getClientInterfaces().add(iface);
						}

						iface.setDevice(dev);
						dev.getInterfaces().add(iface);
						networkModel.getNetworkElements().add(iface);

						listInterface.add(iface);
					}
				}
			}
		}
		return listInterface;
	}

	/**
	 * Check if exists the physical interface in the model
	 * 
	 * @param listInterface
	 * @param addResourceName
	 * @return true if exists the physical interface
	 */
	private static boolean existsdPhysicalInterface(List<Interface> listInterface, String ifaceName) {
		boolean exists = false;
		for (Interface iface : listInterface) {
			if (iface.getName().equals(ifaceName)) {
				exists = true;
			}
		}
		return exists;
	}

	/**
	 * Check if the port have VLAN endpoints
	 * 
	 * @param port
	 * @return true if have VLAN endpoints
	 */
	private static boolean haveVLANEndpoints(NetworkPort port) {
		boolean haveVLANEnpoints = false;
		for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
			if (endpoint instanceof VLANEndpoint) {
				haveVLANEnpoints = true;
			}
		}
		return haveVLANEnpoints;
	}

	/**
	 * Get the interface by name, if not exists return null
	 * 
	 * @param list
	 * @param addResourceName
	 * @return the interface with name = name
	 */
	private static Interface getInterfaceByName(List<NetworkElement> list, String name) {
		Interface _interface = null;
		for (NetworkElement networkElement : list) {
			if (networkElement instanceof Interface) {
				if (networkElement.getName() != null
						&& networkElement.getName().equals(name)) {
					_interface = (Interface) networkElement;
					break;
				}
			}
		}
		return _interface;
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
				if (!linkedPorts.contains(port)) {

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

	/**
	 * @param networkModel
	 * @return
	 */
	private static Layer obtainEthernetLayer(NetworkModel networkModel) {
		for (Layer layer : NetworkModelHelper.getLayers(networkModel)) {
			if (layer instanceof EthernetLayer) {
				return layer;
			}
		}
		return new EthernetLayer();
	}

	/**
	 * @param networkModel
	 * @return
	 */
	private static Layer obtainIPLayer(NetworkModel networkModel) {
		for (Layer layer : NetworkModelHelper.getLayers(networkModel)) {
			if (layer instanceof IPLayer) {
				return layer;
			}
		}
		return new IPLayer();
	}

	/**
	 * @param networkModel
	 * @return
	 */
	private static Layer obtainTaggedEthernetLayer(NetworkModel networkModel) {
		for (Layer layer : NetworkModelHelper.getLayers(networkModel)) {
			if (layer instanceof TaggedEthernetLayer) {
				return layer;
			}
		}
		return new TaggedEthernetLayer();
	}
}
