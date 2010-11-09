package net.i2cat.mantychore.commandsets.junos.digester;

import net.i2cat.mantychore.models.router.StaticRoute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticRouteParser {
	/** logger **/
	Logger			log								= LoggerFactory
															.getLogger(StaticRouteParser.class);

	static String	IpAddressDestinationSubNetwork	= "red";
	static String	IpAddressNextHop				= "hop";

	// StaticRoute staticRoute = new StaticRoute();

	public StaticRouteParser() {

	}

	public void setIpAddressDestinationSubNetwork(
			String IpAddressDestinationSubNetwork) {
		log.debug("IpAddressDestinationSubNetwork: "
				+ IpAddressDestinationSubNetwork);
		this.IpAddressDestinationSubNetwork = IpAddressDestinationSubNetwork;

	}

	public void setIpAddressNextHop(String IpAddressNextHop) {
		log.debug("IpAddressNextHop: " + IpAddressNextHop);
		this.IpAddressNextHop = IpAddressNextHop;
	}

	// public String getIpAddressDestinationSubNetwork() {
	// return IpAddressDestinationSubNetwork;
	//
	// }
	//
	// public String getIpAddressNextHop() {
	// return IpAddressNextHop;
	//
	// }

	public StaticRoute getStaticRoute() {
		StaticRoute staticRoute = new StaticRoute();
		// formating information

		staticRoute
				.setDestinationNetworkIPAddress(IpAddressDestinationSubNetwork);
		staticRoute.setNextHopIPAddress(IpAddressNextHop);
		staticRoute.setIsIPv6(false);

		return staticRoute;
	}

}
