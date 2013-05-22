package org.opennaas.extensions.vcpe.manager.templates.mp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.IPNetworkDomain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class MPTemplateDefaultValuesLoader {

	private static final String			CONFIGURATION_PID	= "org.opennaas.extensions.vcpe.manager.templates.mp.suggestor.defaults";

	/**
	 * Maps TemplateName to properties name.
	 */
	private static Map<String, String>	propertiesNameMap	= new HashMap<String, String>();

	// initialize propertiesNameMap with values in configuration
	static {
		// Physical elements

		propertiesNameMap.put(TemplateConstants.ROUTER_1_PHY, "vcpe.router.1.phy");
		propertiesNameMap.put(TemplateConstants.ROUTER_1_PHY_IFACE_UP1, "vcpe.router.1.phy.iface.phy.up1");
		propertiesNameMap.put(TemplateConstants.ROUTER_1_PHY_IFACE_UP2, "vcpe.router.1.phy.iface.phy.up2");
		propertiesNameMap.put(TemplateConstants.ROUTER_1_PHY_IFACE_DOWN, "vcpe.router.1.phy.iface.phy.down");
		propertiesNameMap.put(TemplateConstants.ROUTER_1_PHY_IFACE_LO, "vcpe.router.1.phy.iface.phy.lo");
		propertiesNameMap.put(TemplateConstants.ROUTER_1_PHY_IFACE_LT, "vcpe.router.1.phy.iface.phy.lt");

		// Logical elements
		propertiesNameMap.put(TemplateConstants.WAN1, "net.wan.1");
		propertiesNameMap.put(TemplateConstants.WAN1_IFACE_DOWN, "net.wan.1.iface.logical.down");

		propertiesNameMap.put(TemplateConstants.WAN2, "net.wan.2");
		propertiesNameMap.put(TemplateConstants.WAN2_IFACE_DOWN, "net.wan.2.iface.logical.down");

		propertiesNameMap.put(TemplateConstants.LAN_CLIENT, "net.lan.client");
		propertiesNameMap.put(TemplateConstants.LAN_CLIENT_IFACE_UP, "net.lan.client.iface.logical.up");

		propertiesNameMap.put(TemplateConstants.LR_1_ROUTER, "vcpe.lr.1");
		propertiesNameMap.put(TemplateConstants.LR_1_IFACE_UP, "vcpe.lr.1.iface.logical.up");
		propertiesNameMap.put(TemplateConstants.LR_1_IFACE_DOWN, "vcpe.lr.1.iface.logical.down");
		propertiesNameMap.put(TemplateConstants.LR_1_IFACE_LO, "vcpe.lr.1.iface.logical.lo");

		propertiesNameMap.put(TemplateConstants.LR_2_ROUTER, "vcpe.lr.2");
		propertiesNameMap.put(TemplateConstants.LR_2_IFACE_UP, "vcpe.lr.2.iface.logical.up");
		propertiesNameMap.put(TemplateConstants.LR_2_IFACE_DOWN, "vcpe.lr.2.iface.logical.down");
		propertiesNameMap.put(TemplateConstants.LR_2_IFACE_LO, "vcpe.lr.2.iface.logical.lo");

		propertiesNameMap.put(TemplateConstants.LR_CLIENT_ROUTER, "vcpe.lr.client");
		propertiesNameMap.put(TemplateConstants.LR_CLIENT_IFACE_UP1, "vcpe.lr.client.iface.logical.up1");
		propertiesNameMap.put(TemplateConstants.LR_CLIENT_IFACE_UP2, "vcpe.lr.client.iface.logical.up2");
		propertiesNameMap.put(TemplateConstants.LR_CLIENT_IFACE_DOWN, "vcpe.lr.client.iface.logical.down");
		propertiesNameMap.put(TemplateConstants.LR_CLIENT_IFACE_LO, "vcpe.lr.client.iface.logical.lo");

	}

	private Properties					props;

	private boolean						initialized;

	public MPTemplateDefaultValuesLoader() {
		setInitialized(false);
	}

	public Properties getProperties() {
		return props;
	}

	public void setProperties(Properties props) {
		this.props = props;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public void initialize() throws IOException {
		initialize(loadProperties());
	}

	public void initialize(Properties properties) {
		setProperties(properties);
		setInitialized(true);
	}

	public VCPENetworkModel loadDefaultPhysicalModel() {
		VCPENetworkModel model = MPTemplateModelBuilder.generatePhysicalElements();
		return loadDefaultPhysicalModel(model);
	}

	/**
	 * @param model
	 *            PhysicalModel containing ALL physical elements required for MPTemplate, and nothing else.
	 * @return given model populated with default values
	 */
	public VCPENetworkModel loadDefaultPhysicalModel(VCPENetworkModel model) {
		for (Router router : VCPENetworkModelHelper.getRouters(model.getElements())) {
			loadPhysicalRouter(router);
		}
		return model;
	}

	public VCPENetworkModel loadDefaultLogicalModel() {
		VCPENetworkModel model = MPTemplateModelBuilder.generateLogicalElements();
		return loadDefaultLogicalModel(model);
	}

	/**
	 * @param model
	 *            LogicalModel containing ALL logical elements required for MPTemplate, and nothing else.
	 * @return given model populated with default values
	 */
	public VCPENetworkModel loadDefaultLogicalModel(VCPENetworkModel model) {
		for (Router router : VCPENetworkModelHelper.getRouters(model.getElements())) {
			loadLogicalRouter(router);
		}

		for (Domain ipNetDomain : VCPENetworkModelHelper.getDomains(model.getElements())) {
			loadIPNetworkDomain((IPNetworkDomain) ipNetDomain);
		}
		return model;
	}

	private Properties loadProperties() throws IOException {
		Properties properties = ConfigurationAdminUtil.getProperties(Activator.getContext(), CONFIGURATION_PID);
		if (properties == null)
			throw new IOException("Failed to initialize template suggestor." + "Unable to obtain configuration " + CONFIGURATION_PID);

		return properties;
	}

	private void loadPhysicalRouter(Router router) {
		router.setName(props.getProperty(propertiesNameMap.get(router.getTemplateName()) + ".name").trim());
		for (Interface iface : router.getInterfaces()) {
			loadPhysicalIface(iface);
		}
	}

	private void loadPhysicalIface(Interface iface) {
		iface.setName(props.getProperty(propertiesNameMap.get(iface.getTemplateName()) + ".name").trim());
		iface.setPhysicalInterfaceName(iface.getName());
	}

	private void loadLogicalRouter(Router router) {
		router.setName(props.getProperty(propertiesNameMap.get(router.getTemplateName()) + ".name").trim());
		for (Interface iface : router.getInterfaces()) {
			loadLogicalIface(iface);
		}
	}

	private void loadIPNetworkDomain(IPNetworkDomain ipNetDomain) {

		String propertiesName = propertiesNameMap.get(ipNetDomain.getTemplateName());

		ipNetDomain.setName(props.getProperty(propertiesName + ".name").trim());
		ipNetDomain.setASNumber(props.getProperty(propertiesName + ".asnum").trim());

		int rangeCount = Integer.parseInt(props.getProperty(propertiesName + ".ipranges.num").trim());
		List<String> ipRanges = new ArrayList<String>(rangeCount);
		for (int i = 0; i < rangeCount; i++) {
			ipRanges.add(props.getProperty(propertiesName + ".ipranges." + i).trim());
		}
		ipNetDomain.setIPAddressRanges(ipRanges);

		for (Interface iface : ipNetDomain.getInterfaces()) {
			loadLogicalIface(iface);
		}
	}

	private void loadLogicalIface(Interface iface) {
		updateInterfaceFromProperties(iface);
	}

	private Interface updateInterfaceFromProperties(Interface iface) {

		String propertiesName = propertiesNameMap.get(iface.getTemplateName());

		long vlan = 0L;
		if (props.getProperty(propertiesName + ".vlan") != null)
			vlan = Long.parseLong(props.getProperty(propertiesName + ".vlan").trim());

		int port = 0;
		if (props.getProperty(propertiesName + ".port") != null)
			port = Integer.parseInt(props.getProperty(propertiesName + ".port").trim());

		String ipAddress = null;
		if (props.getProperty(propertiesName + ".ipaddress") != null)
			ipAddress = props.getProperty(propertiesName + ".ipaddress").trim();

		return VCPENetworkModelHelper.updateInterface(iface,
				props.getProperty(propertiesName + ".name").trim() + "." + port,
				vlan,
				ipAddress,
				props.getProperty(propertiesName + ".name").trim(),
				port);
	}

}
