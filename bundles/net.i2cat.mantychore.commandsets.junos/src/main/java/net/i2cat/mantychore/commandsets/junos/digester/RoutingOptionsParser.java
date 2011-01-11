package net.i2cat.mantychore.commandsets.junos.digester;

import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NextHopIPRoute;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class RoutingOptionsParser extends DigesterEngine {

	class ParserRuleSet extends RuleSetBase {
		private String	prefix	= "";

		protected ParserRuleSet() {

		}

		protected ParserRuleSet(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public void addRuleInstances(Digester arg0) {
			// FIXME the path pattern can't be global , must distinguish between
			// routers
			addObjectCreate("*/routing-options/static/route", NextHopIPRoute.class);
			addMyRule("*/routing-options/static/route/name", "setDestinationAddress", 1);
			addMyRule("*/routing-options/static/route/next-hop", "setNextHop", 1);
			/* Add NesxtHopIpRoute to the parent */
			addSetNext("*/routing-options/static/route", "addInterface");
		}
	}

	public RoutingOptionsParser() {
		ruleSet = new ParserRuleSet();
	}

	public RoutingOptionsParser(String prefix) {
		ruleSet = new ParserRuleSet(prefix);
	}

	public void addInterface(NextHopIPRoute ipRoute) {
		String location = ipRoute.getDestinationAddress();
		// TODO implements the case where is needed merge the classes, if it is.
		// also, add merge method into the class
		mapElements.put(location, ipRoute);

	}

	/* Parser methods */

	public void setDestinationAddress(String ipv4) {

		NextHopIPRoute ipRoute = (NextHopIPRoute) peek();
		try {

			// Parsing ip/mask
			String shortMask = ipv4.split("/")[1];
			String ip = ipv4.split("/")[0];
			String maskIpv4 = IPUtilsHelper.parseShortToLongIpv4NetMask(shortMask);

			// adding to the model
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
		// creating IpEndPoint that will be the next hop
		IPProtocolEndpoint ipNextHop = new IPProtocolEndpoint();
		ipNextHop.setIPv4Address(nextHop);
		try {
			// adding next hop to the destination address (NextHopIPRoute class)
			nextHopIPRoute.setRouteUsesEndpoint(ipNextHop);
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
			str += "Next hop " + ((IPProtocolEndpoint) port.getRouteUsesEndpoint()).getIPv4Address() + '\n';
		}
		return str;
	}
}
