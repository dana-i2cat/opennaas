package net.i2cat.mantychore.network.model;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.network.model.domain.NetworkDomain;
import net.i2cat.mantychore.network.model.layer.Layer;
import net.i2cat.mantychore.network.model.topology.ConnectionPoint;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.Interface;
import net.i2cat.mantychore.network.model.topology.Link;
import net.i2cat.mantychore.network.model.topology.NetworkElement;
import net.i2cat.mantychore.network.model.topology.TransportNetworkElement;

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

	public static int getNetworkElementByName(String name, List<NetworkElement> networkElements) {
		int pos = 0;
		for (NetworkElement networkElement : networkElements) {
			if (networkElement.getName().equals(name))
				return pos;
			pos++;
		}
		return -1;
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

}
