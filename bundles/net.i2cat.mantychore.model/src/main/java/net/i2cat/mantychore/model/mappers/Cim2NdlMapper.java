package net.i2cat.mantychore.model.mappers;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ManagedElement;
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
	 * @return NetworkElements created to represent given model in networkModel.
	 */
	public static List<NetworkElement> addModelToNetworkModel(IModel model, NetworkModel networkModel) {
		if (model instanceof ManagedElement) {
			return addManagedElementToNetworkModel((ManagedElement) model, networkModel);
		}
		return new ArrayList<NetworkElement>();
	}

	/**
	 * Transforms given managedElement to a NDL representation and adds it to networkModel.
	 * 
	 * @param managedElement
	 * @param networkModel
	 * @return NetworkElements created to represent given managedElement in networkModel.
	 */
	private static List<NetworkElement> addManagedElementToNetworkModel(ManagedElement managedElement, NetworkModel networkModel) {

		List<NetworkElement> createdElements = new ArrayList<NetworkElement>();

		if (managedElement instanceof System) {

			// create device
			Device dev = addDeviceToNetworkModel(managedElement, networkModel);

			// create interfaces
			List<Interface> interfaces = addInterfacesToNetworkModel(managedElement, dev, networkModel);

			// create links
			List<Link> links = addLTLinksToNetworkModel(managedElement, interfaces, networkModel);

			createdElements.add(dev);
			createdElements.addAll(interfaces);
			createdElements.addAll(links);
		}
		return createdElements;
	}

	/**
	 * Creates a Device representing given ManagedElement and adds it to networkModel. Notice that a single Device is not a complete representation of
	 * a ManagedElement.
	 * 
	 * @param managedElement
	 * @param networkModel
	 * @return Created Device.
	 */
	private static Device addDeviceToNetworkModel(ManagedElement managedElement, NetworkModel networkModel) {
		Device dev = new Device();
		dev.setName(managedElement.getElementName());
		networkModel.getNetworkElements().add(dev);

		return dev;
	}

	/**
	 * Creates interfaces representing given managedElement endpoints, and adds them to networkModel.
	 * 
	 * @param managedElement
	 * @param dev
	 * @param networkModel
	 * @return Created interfaces.
	 */
	private static List<Interface> addInterfacesToNetworkModel(ManagedElement managedElement, Device dev, NetworkModel networkModel) {

		List<Interface> interfaces = new ArrayList<Interface>();

		for (NetworkPort port : ModelHelper.getInterfaces((System) managedElement)) {
			Interface topIface = generateInterfaceBasedOnPortType(port, dev, networkModel);
			interfaces.add(topIface);

			if (port.getProtocolEndpoint() != null) {

				// add VLAN interfaces
				for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
					if (endpoint instanceof VLANEndpoint) {
						// eth over eth
						Interface iface = new Interface();
						iface.setName(port.getName() + "." + port.getPortNumber());

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
						iface.setName(((IPProtocolEndpoint) endpoint).getIPv4Address());

						Layer ifaceLayer = obtainIPLayer(networkModel);
						iface.setLayer(ifaceLayer);

						// find server interface
						// traverse interfaces in reverse order to find VLAN interfaces before pure eth ifaces
						Interface serverInterface = null;
						for (int i = interfaces.size() - 1; i >= 0 && serverInterface == null; i--) {
							if (interfaces.get(i).getName().equals(port.getName() + "." + port.getPortNumber())) {
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
		return interfaces;
	}

	private static Interface generateInterfaceBasedOnPortType(NetworkPort port, Device dev, NetworkModel networkModel) {

		Interface iface = null;
		if (port instanceof EthernetPort || port instanceof LogicalTunnelPort) {

			iface = new Interface();
			iface.setName(port.getName() + "." + port.getPortNumber());

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
	private static List<Link> addLTLinksToNetworkModel(ManagedElement managedElement, List<Interface> interfaces, NetworkModel networkModel) {

		List<LogicalTunnelPort> linkedPorts = new ArrayList<LogicalTunnelPort>();
		List<Link> links = new ArrayList<Link>();

		for (NetworkPort port : ModelHelper.getInterfaces((System) managedElement)) {
			if (port instanceof LogicalTunnelPort) {
				// avoid duplicating links
				if (!linkedPorts.contains((LogicalTunnelPort) port)) {

					// get lt endpoints
					LogicalTunnelPort srcPort = (LogicalTunnelPort) port;
					LogicalTunnelPort dstPort = null;
					long dstPortUnit = srcPort.getPeer_unit();
					for (NetworkPort otherPort : ModelHelper.getInterfaces((System) managedElement)) {
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
							if (anIface.getName().equals(srcPort.getName() + "." + srcPort.getPortNumber())) {
								srcIface = anIface;

								// get interface connected to srcIface
								for (Interface otherIface : interfaces) {
									if (otherIface.getName().equals(dstPort.getName() + "." + dstPort.getPortNumber())) {
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
