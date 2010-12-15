package net.i2cat.mantychore.commandsets.junos.digester;

import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NextHopIPRoute;

public class RoutingOptionsParser extends DigesterEngine {

	@Override
	public void addRules() {

		// FIXME the path pattern can't be global , must distinguish between routers
		addObjectCreate("*/routing-options/static/route", NextHopIPRoute.class);
		addMyRule("*/routing-options/static/route/name", "setDestinationAddress", 1);
		addMyRule("*/routing-options/static/route/next-hop", "setNextHop", 1);
		/* Add NesxtHopIpRoute to the parent */
		addSetNext("*/routing-options/static/route", "addInterface");

	}

	public void addInterface(NextHopIPRoute ipRoute) {
		String location = ipRoute.getDestinationAddress();
		// TODO implements the case where is needed merge the clases if it is.
		// if (mapElements.containsKey(location)) {
		// NextHopIPRoute oldIpROute = (NextHopIPRoute) mapElements.get(location);
		// // ipRoute.merge(hashEthernetPort);
		// oldIpROute= ipRoute;
		// mapElements.put(key, value)
		// mapElements.remove(location);
		// }
		mapElements.put(location, ipRoute);

	}

	/* Parser methods */

	public void setDestinationAddress(String ipv4) {

		NextHopIPRoute ipRoute = (NextHopIPRoute) peek();
		try {
			String shortMask = ipv4.split("/")[1];
			String ip = ipv4.split("/")[0];
			String maskIpv4 = IPUtilsHelper.parseShortToLongIpv4NetMask(shortMask);
			ipRoute.setDestinationAddress(ip);
			ipRoute.setPrefixLength(Byte.parseByte(shortMask));
			ipRoute.setDestinationMask(maskIpv4);
			ipRoute.setIsStatic(true);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void setNextHop(String nextHop) {

		NextHopIPRoute nextHopIPRoute = (NextHopIPRoute) peek(0);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address(nextHop);
		try {
			nextHopIPRoute.setRouteUsesEndPoint(ip);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public String toPrint() {

		String str = "" + '\n';
		for (String key : mapElements.keySet()) {
			NextHopIPRoute port = (NextHopIPRoute) mapElements.get(key);
			str += "- Routing options " + '\n';
			str += "IP adress " + port.getDestinationAddress() + '\n';
			str += "Mask " + port.getDestinationMask() + '\n';
			str += "is Static " + String.valueOf(port.isIsStatic()) + '\n';
			str += "Next hop " + port.getRouteUsesEndPoint().getIPv4Address() + '\n';
		}
		return str;
	}
}
