package org.opennaas.extensions.router.junos.commandsets.digester;

import java.util.HashMap;

import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NextHopIPRoute;
import org.opennaas.extensions.router.model.NextHopRoute;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.RouteCalculationService;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class RoutingOptionsParser extends DigesterEngine {

	private System	model;

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
			addMyRule("*/routing-options/router-id", "setRouterIdInServices", 0);
			addObjectCreate("*/routing-options/static/route", NextHopIPRoute.class);
			addMyRule("*/routing-options/static/route/name", "setDestinationAddress", 0);
			// addMyRule("*/routing-options/static/route/next-hop", "setNextHop", 0);
			/* Add NextHopIPRoute to mapElements */
			/* AddRouteUsesEndpoint */
			addMyRule("*/routing-options/static/route/next-hop", "addInterface", 0);

			/* Add NextHopIpRoute to the parent */
			addSetNext("*/routing-options/static/route/", "addNextHopRoute");
		}
	}

	public RoutingOptionsParser(System model) {
		ruleSet = new ParserRuleSet();
		setModel(model);
	}

	public RoutingOptionsParser(String prefix, System model) {
		ruleSet = new ParserRuleSet(prefix);
		setModel(model);
	}

	@Override
	public void init() {
		// puts this and model in the stack
		push(this); // next-to-top
		push(model);// top
		mapElements = new HashMap<String, Object>();
	}

	/*
	 * public void addInterface(String nextHop) { NextHopIPRoute ipRoute = (NextHopIPRoute) peek(0);
	 * 
	 * String location = ipRoute.getDestinationAddress(); // TODO implements the case where is needed merge the classes, if it is. // also, add merge
	 * method into the class mapElements.put(location, ipRoute);
	 * 
	 * }
	 */
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
			// adding association to IPProtocolEndpoint
			nextHopIPRoute.setProtocolEndpoint(ipNextHop);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Look for RouteCalculationServices in the model and set RouterId in them.
	 * 
	 * Assumes a System is in the top of the stack.
	 * 
	 * @param routerId
	 */
	public void setRouterIdInServices(String routerId) {
		Object obj = peek(0);
		assert (obj instanceof System);
		System routerModel = (System) obj;

		for (Service service : routerModel.getHostedService()) {
			if (service instanceof RouteCalculationService) {
				((RouteCalculationService) service).setRouterID(routerId);
			}
		}
	}

	public void addInterface(String nextHop) {
		NextHopIPRoute nextHopIPRoute = (NextHopIPRoute) peek(0);

		boolean isGRE = false;
		for (Service service : model.getHostedService()) {
			if (service instanceof GRETunnelService) {
				GRETunnelService greService = (GRETunnelService) service;
				String name = service.getName() + ".0";
				if (name.equals(nextHop)) {
					for (ProtocolEndpoint pE : greService.getProtocolEndpoint()) {
						if (pE instanceof GRETunnelEndpoint) {
							GRETunnelEndpoint gE = (GRETunnelEndpoint) pE;
							nextHopIPRoute.setProtocolEndpoint(pE);
							isGRE = true;

						}

					}
				}
			}
		}
		if (!isGRE) {
			for (LogicalDevice device : model.getLogicalDevices()) {
				if (device instanceof NetworkPort) {

					NetworkPort port = (NetworkPort) device;
					String ifacename = port.getName() + "." + String.valueOf(port.getPortNumber());

					if (ifacename.equals(nextHop)) {
						for (ProtocolEndpoint pE : port.getProtocolEndpoint()) {
							if (pE instanceof IPProtocolEndpoint) {
								IPProtocolEndpoint iE = (IPProtocolEndpoint) pE;
								nextHopIPRoute.setProtocolEndpoint(iE);
							}
						}

					}
				}
			}
		}

	}

	public String toPrint(System model) {
		String str = "";
		str += model.getNextHopRoute().size();
		for (NextHopRoute nextHop : model.getNextHopRoute()) {
			NextHopIPRoute port = (NextHopIPRoute) nextHop;
			str += "- Routing options " + '\n';

			str += "IP adress " + port.getDestinationAddress() + '\n';
			str += "Mask " + port.getDestinationMask() + '\n';
			str += "is Static " + String.valueOf(port.isIsStatic()) + '\n';
			if (port.getProtocolEndpoint() instanceof GRETunnelEndpoint)
				str += "Next hop " + (((GRETunnelEndpoint) port.getProtocolEndpoint()).getIPv4Address()) + '\n';
			else if (port.getProtocolEndpoint() instanceof IPProtocolEndpoint)
				str += "Next hop " + (((IPProtocolEndpoint) port.getProtocolEndpoint()).getIPv4Address()) + '\n';
		}

		return str;

	}

	@Deprecated
	public String toPrint() {

		String str = "" + '\n';
		for (String key : mapElements.keySet()) {
			NextHopIPRoute port = (NextHopIPRoute) mapElements.get(key);
			str += "- Routing options " + '\n';
			str += "IP adress " + port.getDestinationAddress() + '\n';
			str += "Mask " + port.getDestinationMask() + '\n';
			str += "is Static " + String.valueOf(port.isIsStatic()) + '\n';
			str += "Next hop " + (((IPProtocolEndpoint) port.getProtocolEndpoint()).getIPv4Address()) + '\n';
		}
		return str;
	}

	public System getModel() {
		return model;
	}

	public void setModel(System model) {
		this.model = model;
	}
}
