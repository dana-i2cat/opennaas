package org.opennaas.extensions.network.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.network.model.layer.Layer;
import org.opennaas.extensions.network.model.topology.ConnectionPoint;
import org.opennaas.extensions.network.model.topology.CrossConnect;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkConnection;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.network.model.topology.Path;
import org.opennaas.extensions.network.model.topology.TransportNetworkElement;

public class NetworkModelHelper {

	public static List<NetworkElement> getNetworkElementsExceptTransportElements(NetworkModel model) {
		return getNetworkElementsExceptTransportElements(model.getNetworkElements());
	}

	public static List<NetworkElement> getNetworkElementsExceptTransportElements(List<NetworkElement> networkElements) {
		List<NetworkElement> toReturn = new ArrayList<NetworkElement>();
		for (NetworkElement elem : networkElements) {
			if (!(elem instanceof TransportNetworkElement)) {
				toReturn.add(elem);
			}
		}
		return toReturn;
	}

	public static List<Device> getDevices(NetworkModel model) {
		return getDevices(model.getNetworkElements());
	}

	public static List<Device> getDevices(List<NetworkElement> networkElements) {
		List<Device> toReturn = new ArrayList<Device>();
		for (NetworkElement elem : networkElements) {
			if (elem instanceof Device) {
				toReturn.add((Device) elem);
			}
		}
		return toReturn;
	}

	public static List<NetworkDomain> getDomains(NetworkModel model) {
		return getDomains(model.getNetworkElements());
	}

	public static List<NetworkDomain> getDomains(List<NetworkElement> networkElements) {
		List<NetworkDomain> toReturn = new ArrayList<NetworkDomain>();
		for (NetworkElement elem : networkElements) {
			if (elem instanceof NetworkDomain) {
				toReturn.add((NetworkDomain) elem);
			}
		}
		return toReturn;
	}

	public static List<Link> getLinks(NetworkModel model) {
		return getLinks(model.getNetworkElements());
	}

	public static List<Link> getLinks(List<NetworkElement> networkElements) {
		List<Link> toReturn = new ArrayList<Link>();
		for (NetworkElement elem : networkElements) {
			if (elem instanceof Link) {
				toReturn.add((Link) elem);
			}
		}
		return toReturn;
	}

	public static List<Layer> getLayers(NetworkModel model) {
		return getLayers(model.getNetworkElements());
	}

	public static List<Layer> getLayers(List<NetworkElement> networkElements) {
		List<Layer> toReturn = new ArrayList<Layer>();
		for (NetworkElement elem : networkElements) {
			if (elem instanceof ConnectionPoint) {
				Layer layer = ((ConnectionPoint) elem).getLayer();
				// TODO implement equals in Layer
				if (!toReturn.contains(layer))
					toReturn.add(layer);
			}
		}
		return toReturn;
	}

	public static List<ConnectionPoint> getConnectionPoints(NetworkModel model) {
		return getConnectionPoints(model.getNetworkElements());
	}

	public static List<ConnectionPoint> getConnectionPoints(List<NetworkElement> networkElements) {
		List<ConnectionPoint> toReturn = new ArrayList<ConnectionPoint>();
		for (NetworkElement elem : networkElements) {
			if (elem instanceof ConnectionPoint) {
				toReturn.add((ConnectionPoint) elem);
			}
		}
		return toReturn;
	}

	public static List<Interface> getInterfaces(List<NetworkElement> networkElements) {
		List<Interface> toReturn = new ArrayList<Interface>();
		for (NetworkElement elem : networkElements) {
			if (elem instanceof Interface) {
				toReturn.add((Interface) elem);
			}
		}
		return toReturn;
	}

	@Deprecated
	public static Interface getInterfaceByName(String interfaceName, NetworkModel model) {
		for (NetworkElement elem : model.getNetworkElements()) {
			if (elem instanceof Interface) {
				if (elem.getName().equals(interfaceName))
					return (Interface) elem;
			}
		}
		return null;
	}

	/**
	 * Get the interface from Network Model and interface name
	 * 
	 * @param networkElements
	 * @return Interface
	 */
	public static Interface getInterfaceByName(List<NetworkElement> networkElements, String interfaceName) {
		Interface toReturn = null;
		for (NetworkElement elem : networkElements) {
			if (elem instanceof Interface && elem.getName().equals(interfaceName)) {
				toReturn = (Interface) elem;
				break;
			}
		}
		return toReturn;
	}

	/**
	 * 
	 * @param name
	 * @param networkElements
	 * @return lowest current position of networkElements containing an element named with given name.
	 */
	public static int getNetworkElementByName(String name, List<NetworkElement> networkElements) {
		int pos = 0;
		for (NetworkElement networkElement : networkElements) {
			if (networkElement.getName() != null
					&& networkElement.getName().equals(name))
				return pos;
			pos++;
		}
		return -1;
	}

	/**
	 * Return all elements in networkElements being of class clazz.
	 * 
	 * @param clazz
	 * @param networkElements
	 * @return
	 */
	public static List<NetworkElement> getNetworkElementsByClassName(Class clazz, List<NetworkElement> networkElements) {
		List<NetworkElement> matchingElements = new ArrayList<NetworkElement>();
		for (NetworkElement networkElement : networkElements) {
			if (networkElement.getClass().equals(clazz))
				matchingElements.add(networkElement);
		}
		return matchingElements;
	}

	public static Link linkInterfaces(Interface src, Interface dst, boolean bidiLink) {
		Link link = new Link();
		link.setSource(src);
		link.setSink(dst);
		link.setBidirectional(bidiLink);

		link.setLayer(src.getLayer());

		src.setLinkTo(link);
		if (bidiLink) {
			dst.setLinkTo(link);
		}
		return link;
	}

	public static CrossConnect crossConnectInterfaces(Interface src, Interface dst) {
		CrossConnect xConnect = new CrossConnect();
		xConnect.setSource(src);
		xConnect.setSink(dst);

		xConnect.setLayer(src.getLayer());

		src.setSwitchedTo(xConnect);
		dst.setSwitchedTo(xConnect);

		return xConnect;
	}

	/**
	 * Removes given NetworkElement from given networkModel (if exists).
	 * 
	 * @param toRemove
	 * @param networkModel
	 * @return updated network model
	 */
	public static NetworkModel deleteNetworkElementAndReferences(NetworkElement toRemove, NetworkModel networkModel) {
		if (!networkModel.getNetworkElements().contains(toRemove)) {
			// TODO throw exception?
			return networkModel;
		}

		if (toRemove instanceof NetworkDomain) {
			while (((NetworkDomain) toRemove).getHasDevice().size() > 0) {
				deleteNetworkElementAndReferences(((NetworkDomain) toRemove).getHasDevice().get(0), networkModel);
			}
			while (((NetworkDomain) toRemove).getHasInterface().size() > 0) {
				deleteNetworkElementAndReferences(((NetworkDomain) toRemove).getHasInterface().get(0), networkModel);
			}

			networkModel.getNetworkElements().remove(toRemove);
			toRemove.setLocatedAt(null);
			toRemove.getInAdminDomains().clear();

		} else if (toRemove instanceof Device) {

			while (((Device) toRemove).getInterfaces().size() > 0) {
				deleteNetworkElementAndReferences(((Device) toRemove).getInterfaces().get(0), networkModel);
			}

			networkModel.getNetworkElements().remove(toRemove);
			toRemove.setLocatedAt(null);
			toRemove.getInAdminDomains().clear();

		} else if (toRemove instanceof TransportNetworkElement) {
			deleteTransportNetworkConnectionAndReferences((TransportNetworkElement) toRemove, networkModel);
		}

		return networkModel;
	}

	public static NetworkModel deleteTransportNetworkConnectionAndReferences(TransportNetworkElement toRemove, NetworkModel networkModel) {

		if (!networkModel.getNetworkElements().contains(toRemove)) {
			// TODO throw exception?
			return networkModel;
		}

		if (toRemove instanceof ConnectionPoint) {

			while (((ConnectionPoint) toRemove).getClientInterfaces().size() > 0) {
				deleteNetworkElementAndReferences(((ConnectionPoint) toRemove).getClientInterfaces().get(0), networkModel);
			}

			if (toRemove instanceof Interface) {

				if (((Interface) toRemove).getDevice() != null)
					((Interface) toRemove).getDevice().getInterfaces().remove(toRemove);

				deleteNetworkElementAndReferences(((Interface) toRemove).getLinkTo(), networkModel);
				// TODO WHAT TO DO WITH PATHS???
				// should they be removed when its source/sink is ?
				// should they be removed when an intermediate links/path is?
				deleteNetworkElementAndReferences(((Interface) toRemove).getConnectedTo(), networkModel);
				deleteNetworkElementAndReferences(((Interface) toRemove).getSwitchedTo(), networkModel);
			}
			if (((ConnectionPoint) toRemove).getServerInterface() != null) {
				((ConnectionPoint) toRemove).getServerInterface().getClientInterfaces().remove(toRemove);
				((ConnectionPoint) toRemove).setServerInterface(null);
			}

			toRemove.setLayer(null);

			networkModel.getNetworkElements().remove(toRemove);
			toRemove.setLocatedAt(null);
			toRemove.getInAdminDomains().clear();

		} else if (toRemove instanceof NetworkConnection) {
			deleteNetworkConnectionAndReferences((NetworkConnection) toRemove, networkModel);
		}

		return networkModel;
	}

	public static NetworkModel deleteNetworkConnectionAndReferences(NetworkConnection toRemove, NetworkModel networkModel) {

		if (!networkModel.getNetworkElements().contains(toRemove)) {
			// TODO throw exception?
			return networkModel;
		}

		if (toRemove instanceof Link) {
			((Link) toRemove).getSource().setLinkTo(null);
			((Link) toRemove).getSink().setLinkTo(null);
			((Link) toRemove).setSource(null);
			((Link) toRemove).setSink(null);

		} else if (toRemove instanceof Path) {
			((Path) toRemove).getSource().setLinkTo(null);
			((Path) toRemove).getSink().setLinkTo(null);
			((Path) toRemove).setSource(null);
			((Path) toRemove).setSink(null);

			// TODO what to do with segments??
			// SHOULD THEY BE REMOVED?
			// intermediate links and paths will still be active

			// don't remove them by now
			// just mark they are no longer part of toRemove
			((Path) toRemove).getPathSegments().clear();

		} else if (toRemove instanceof CrossConnect) {
			((CrossConnect) toRemove).getSource().setSwitchedTo(null);
			((CrossConnect) toRemove).getSink().setSwitchedTo(null);
			((CrossConnect) toRemove).setSource(null);
			((CrossConnect) toRemove).setSink(null);
		}

		toRemove.setLayer(null);

		networkModel.getNetworkElements().remove(toRemove);
		toRemove.setLocatedAt(null);
		toRemove.getInAdminDomains().clear();

		return networkModel;
	}
}
