package org.opennaas.extensions.router.junos.commandsets.digester;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NextHopIPRoute;
import org.opennaas.extensions.router.model.NextHopRoute;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.RouteCalculationService;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

import com.google.common.net.InetAddresses;

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
			// FIXME the path pattern can't be global, must distinguish between routers
			addMyRule("*/routing-options/router-id", "setRouterIdInServices", 0);

			// ********************
			// IPv4 static routes *
			// ********************
			addObjectCreate("*/routing-options/static/route", NextHopIPRoute.class);
			addMyRule("*/routing-options/static/route/name", "setDestinationAddress", 0);
			addMyRule("*/routing-options/static/route/next-hop", "setNextHop", 0);
			addCallMethod("*/routing-options/static/route/preference/metric-value", "setRouteMetric", 0, new Class[] { Integer.TYPE });

			/* Add NextHopIpRoute to the parent */
			addSetNext("*/routing-options/static/route/", "addNextHopRoute");

			// ********************
			// IPv6 static routes *
			// ********************
			addObjectCreate("*/routing-options/rib/static/route", NextHopIPRoute.class);
			addMyRule("*/routing-options/rib/static/route/name", "setDestinationAddress", 0);
			addMyRule("*/routing-options/rib/static/route/next-hop", "setNextHop", 0);
			addCallMethod("*/routing-options/rib/static/route/preference/metric-value", "setRouteMetric", 0, new Class[] { Integer.TYPE });
			addCallMethod("*/routing-options/rib/static/route/discard", "setRouteMetric", 0, new Class[] { Integer.TYPE });

			/* Add NextHopIpRoute to the parent */
			addSetNext("*/routing-options/rib/static/route/", "addNextHopRoute");
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
	 * String location = ipRoute.getDestinationAddress();
	 * 
	 * TODO implements the case where is needed merge the classes, if it is. also, add merge method into the class mapElements.put(location, ipRoute);
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
		// ***************************
		// Next hop is an IP address *
		// ***************************
		// creating IpEndPoint that will be the next hop
		IPProtocolEndpoint ipNextHop = new IPProtocolEndpoint();
		if (InetAddresses.isInetAddress(nextHop)) {
			if (IPUtilsHelper.validateIpAddressPattern(nextHop)) {
				ipNextHop.setProtocolIFType(ProtocolIFType.IPV4);
				ipNextHop.setIPv4Address(nextHop);
			} else {
				ipNextHop.setProtocolIFType(ProtocolIFType.IPV6);
				ipNextHop.setIPv6Address(nextHop);
			}
		} else {
			// not an IP address, it must be an interface.unit
			addInterface(nextHop);
			return;
		}
		try {
			// adding association to IPProtocolEndpoint
			nextHopIPRoute.setProtocolEndpoint(ipNextHop);
		} catch (Exception e) {
			log.error(e.getMessage());
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
							nextHopIPRoute.setProtocolEndpoint(gE);
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

	public String toPrint(System model) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(model.getNextHopRoute().size());
		for (NextHopRoute nextHop : model.getNextHopRoute()) {
			NextHopIPRoute port = (NextHopIPRoute) nextHop;
			strBuilder.append("- Routing options\n");

			strBuilder.append("IP adress " + port.getDestinationAddress() + '\n');
			strBuilder.append("Mask " + port.getDestinationMask() + '\n');
			strBuilder.append("is Static " + String.valueOf(port.isIsStatic()) + '\n');
			if (port.getProtocolEndpoint() instanceof GRETunnelEndpoint)
				strBuilder.append("Next hop " + (((GRETunnelEndpoint) port.getProtocolEndpoint()).getIPv4Address()) + '\n');
			else if (port.getProtocolEndpoint() instanceof IPProtocolEndpoint)
				strBuilder.append("Next hop " + (((IPProtocolEndpoint) port.getProtocolEndpoint()).getIPv4Address()) + '\n');
		}

		return strBuilder.toString();

	}

	public System getModel() {
		return model;
	}

	public void setModel(System model) {
		this.model = model;
	}
}
