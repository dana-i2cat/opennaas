package net.i2cat.mantychore.commandsets.digester;

import net.i2cat.mantychore.models.router.RouterModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterParser {
	/** logger **/
	Logger						log	= LoggerFactory
											.getLogger(RouterParser.class);

	private static RouterModel	routerModel;

	public RouterParser() {
	}

	public void setHostname(String hostname) {
		log.debug("hostname: " + hostname);
		routerModel.setHostName(hostname);

	}

	public void setVersion(String version) {
		log.debug("version: " + version);
		routerModel.setVersionOS(version);
	}

	public void addPhysicalInterface(InterfaceParser interfaceParser) {
		log.debug("interface location: "
				+ interfaceParser.getPhysicalInterface().getLocation());
		// routerModel.addPhysicalInterface(interfaceParser.getPhysicalInterface());

	}

	public static RouterModel getRouterModel() {
		return routerModel;
	}

	public static void setRouterModel(RouterModel routerModel) {
		RouterParser.routerModel = routerModel;
	}

}
