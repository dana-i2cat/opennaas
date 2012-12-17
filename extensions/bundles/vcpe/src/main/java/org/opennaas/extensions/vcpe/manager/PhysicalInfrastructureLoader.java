package org.opennaas.extensions.vcpe.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.opennaas.extensions.vcpe.manager.model.VCPEPhysicalInfrastructure;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class PhysicalInfrastructureLoader {

	private static final String	PROPERTIES_PATH	= "/templates/phyInfrastructure.properties";

	public VCPEPhysicalInfrastructure loadPhysicalInfrastructure() throws IOException {
		Properties props = loadProperties();
		return loadPhysicalInfrastuctureFromProperties(props);
	}

	private Properties loadProperties() throws IOException {
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream(PROPERTIES_PATH));
		return props;
	}

	private VCPEPhysicalInfrastructure loadPhysicalInfrastuctureFromProperties(Properties props) {

		List<Interface> allIfaces = new ArrayList<Interface>();

		// load routers
		int routersSize = Integer.parseInt(props.getProperty("phy.routers").trim());
		List<Router> routers = new ArrayList<Router>(routersSize);
		for (int i = 0; i < routersSize; i++) {
			String routerNS = "phy.router." + i;

			Router router = new Router();
			router.setName(props.getProperty(routerNS + ".name"));
			router.setTemplateName(routerNS);
			List<Interface> routerIfaces = loadInterfacesFromProperties(props, routerNS);
			router.setInterfaces(routerIfaces);
			routers.add(router);
			allIfaces.addAll(routerIfaces);
		}

		// load BoDs
		int bodsSize = Integer.parseInt(props.getProperty("phy.bods").trim());
		List<Domain> bods = new ArrayList<Domain>(bodsSize);
		for (int i = 0; i < bodsSize; i++) {
			String bodNS = "phy.bod." + i;

			Domain domain = new Domain();
			domain.setName(props.getProperty(bodNS + ".name"));
			domain.setTemplateName(bodNS);
			List<Interface> bodIfaces = loadInterfacesFromProperties(props, bodNS);
			domain.setInterfaces(bodIfaces);
			bods.add(domain);
			allIfaces.addAll(bodIfaces);
		}

		// load Links
		int linksSize = Integer.parseInt(props.getProperty("phy.links").trim());
		List<Link> links = new ArrayList<Link>(linksSize);
		for (int i = 0; i < bodsSize; i++) {
			String linkNS = "phy.link." + i;
			Link link = new Link();
			link.setTemplateName(linkNS);
			link.setName(props.getProperty(linkNS + ".name"));
			link.setSource((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, props.getProperty(linkNS + ".source")));
			link.setSink((Interface) VCPENetworkModelHelper.getElementByTemplateName(allIfaces, props.getProperty(linkNS + ".dst")));
			links.add(link);
		}

		return createPhysicalInfrastructure(routers, bods, links);
	}

	private List<Interface> loadInterfacesFromProperties(Properties props, String namespace) {

		int ifacesSize = Integer.parseInt(props.getProperty(namespace + ".ifaces").trim());
		List<Interface> ifaces = new ArrayList<Interface>(ifacesSize);

		for (int i = 0; i < ifacesSize; i++) {
			String ifaceNS = namespace + ".iface." + i;
			Interface iface = new Interface();
			iface.setName(props.getProperty(ifaceNS));
			iface.setPhysicalInterfaceName(props.getProperty(ifaceNS));
			iface.setTemplateName(ifaceNS);

			ifaces.add(iface);
		}

		return ifaces;
	}

	private VCPEPhysicalInfrastructure createPhysicalInfrastructure(List<Router> routers, List<Domain> domains, List<Link> links) {
		// TODO Update PhysicalInsfrasctucture class to support having multiple routers and multiple BoDs
		VCPEPhysicalInfrastructure phy = new VCPEPhysicalInfrastructure();

		phy.setPhyRouterCore(routers.get(0));
		phy.setPhyRouterMaster(routers.get(1));
		phy.setPhyRouterBackup(routers.get(2));

		phy.setPhyBoD(domains.get(0));

		phy.setPhyLinks(links);

		return phy;
	}

}
