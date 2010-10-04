package net.i2cat.mantychore.commandsets.digester;

import net.i2cat.mantychore.models.router.RouterModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticRouteParser {
	/** logger **/
	Logger						log	= LoggerFactory
											.getLogger(StaticRouteParser.class);

	private static RouterModel	routerModel;

	public StaticRouteParser() {
	}

	public void setIpAddressDestinationSubNetwork(
			String IpAddressDestinationSubNetwork) {
		log.debug("IpAddressDestinationSubNetwork: "
				+ IpAddressDestinationSubNetwork);
		routerModel.setHostName(IpAddressDestinationSubNetwork);

	}

	public void setIpAddressNextHop(String IpAddressNextHop) {
		log.debug("IpAddressNextHop: " + IpAddressNextHop);
		routerModel.setVersionOS(IpAddressNextHop);
	}

	public static RouterModel getRouterModel() {
		return routerModel;
	}

	public static void setRouterModel(RouterModel routerModel) {
		StaticRouteParser.routerModel = routerModel;
	}

}
