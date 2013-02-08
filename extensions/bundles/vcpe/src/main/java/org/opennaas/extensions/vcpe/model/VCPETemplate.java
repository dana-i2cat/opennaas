package org.opennaas.extensions.vcpe.model;

public class VCPETemplate {

	public static final String	TEMPLATE_NS						= "org.opennaas.extensions.vcpe.template.1";

	public static final String	CORE_PHY_ROUTER					= TEMPLATE_NS + ".core.phy.router";
	public static final String	CORE_PHY_INTERFACE_MASTER		= TEMPLATE_NS + ".core.master.phy.iface.local";
	public static final String	CORE_PHY_INTERFACE_BKP			= TEMPLATE_NS + ".core.backup.phy.iface.local";
	public static final String	CORE_PHY_LO_INTERFACE			= TEMPLATE_NS + ".core.lo.phy.iface";

	public static final String	CPE1_PHY_ROUTER					= TEMPLATE_NS + ".cpe1.phy.router";
	public static final String	INTER1_PHY_INTERFACE_LOCAL		= TEMPLATE_NS + ".cpe1.inter.phy.iface.local";
	public static final String	INTER1_PHY_INTERFACE_AUTOBAHN	= TEMPLATE_NS + ".cpe1.inter.phy.iface.other";
	public static final String	DOWN1_PHY_INTERFACE_LOCAL		= TEMPLATE_NS + ".cpe1.down.phy.iface.local";
	public static final String	DOWN1_PHY_INTERFACE_AUTOBAHN	= TEMPLATE_NS + ".cpe1.down.phy.iface.local.other";
	public static final String	CLIENT1_PHY_INTERFACE_AUTOBAHN	= TEMPLATE_NS + ".cpe1.client.phy.iface.other";
	public static final String	UP1_PHY_INTERFACE_LOCAL			= TEMPLATE_NS + ".cpe1.up.phy.iface.local";
	public static final String	LO1_PHY_INTERFACE				= TEMPLATE_NS + ".cpe1.lo.phy.iface";

	public static final String	CPE2_PHY_ROUTER					= TEMPLATE_NS + ".cpe2.phy.router";
	public static final String	INTER2_PHY_INTERFACE_LOCAL		= TEMPLATE_NS + ".cpe2.inter.phy.iface.local";
	public static final String	INTER2_PHY_INTERFACE_AUTOBAHN	= TEMPLATE_NS + ".cpe2.inter.phy.iface.other";
	public static final String	DOWN2_PHY_INTERFACE_LOCAL		= TEMPLATE_NS + ".cpe2.down.phy.iface.local";
	public static final String	DOWN2_PHY_INTERFACE_AUTOBAHN	= TEMPLATE_NS + ".cpe2.down.phy.iface.local.other";
	public static final String	CLIENT2_PHY_INTERFACE_AUTOBAHN	= TEMPLATE_NS + ".cpe2.client.phy.iface.other";
	public static final String	UP2_PHY_INTERFACE_LOCAL			= TEMPLATE_NS + ".cpe2.up.phy.iface.local";
	public static final String	LO2_PHY_INTERFACE				= TEMPLATE_NS + ".cpe2.lo.phy.iface";

	public static final String	CORE_LO_INTERFACE				= TEMPLATE_NS + ".core.lo.iface";

	public static final String	VCPE1_ROUTER					= TEMPLATE_NS + ".vcpe1.router";
	public static final String	INTER1_INTERFACE_LOCAL			= TEMPLATE_NS + ".vcpe1.inter.iface.local";
	public static final String	INTER1_INTERFACE_AUTOBAHN		= TEMPLATE_NS + ".vcpe1.inter.iface.other";
	public static final String	DOWN1_INTERFACE_LOCAL			= TEMPLATE_NS + ".vcpe1.down.iface.local";
	public static final String	DOWN1_INTERFACE_AUTOBAHN		= TEMPLATE_NS + ".vcpe1.down.iface.other";
	public static final String	UP1_INTERFACE_LOCAL				= TEMPLATE_NS + ".vcpe1.up.iface.local";
	public static final String	UP1_INTERFACE_PEER				= TEMPLATE_NS + ".vcpe1.up.iface.other";
	public static final String	LO1_INTERFACE					= TEMPLATE_NS + ".vcpe1.lo.iface";

	public static final String	VCPE2_ROUTER					= TEMPLATE_NS + ".vcpe2.router";
	public static final String	INTER2_INTERFACE_LOCAL			= TEMPLATE_NS + ".vcpe2.inter.iface.local";
	public static final String	INTER2_INTERFACE_AUTOBAHN		= TEMPLATE_NS + ".vcpe2.inter.iface.other";
	public static final String	DOWN2_INTERFACE_LOCAL			= TEMPLATE_NS + ".vcpe2.down.iface.local";
	public static final String	DOWN2_INTERFACE_AUTOBAHN		= TEMPLATE_NS + ".vcpe2.down.iface.other";
	public static final String	UP2_INTERFACE_LOCAL				= TEMPLATE_NS + ".vcpe2.up.iface.local";
	public static final String	UP2_INTERFACE_PEER				= TEMPLATE_NS + ".vcpe2.up.iface.other";
	public static final String	LO2_INTERFACE					= TEMPLATE_NS + ".vcpe2.lo.iface";

	public static final String	CLIENT1_INTERFACE_AUTOBAHN		= TEMPLATE_NS + ".vcpe1.client.iface.other";
	public static final String	CLIENT2_INTERFACE_AUTOBAHN		= TEMPLATE_NS + ".vcpe2.client.iface.other";

	public static final String	INTER1_LINK_LOCAL				= TEMPLATE_NS + ".link.inter.local.1";
	public static final String	INTER2_LINK_LOCAL				= TEMPLATE_NS + ".link.inter.local.2";
	public static final String	INTER_LINK_AUTOBAHN				= TEMPLATE_NS + ".link.inter.other";
	public static final String	DOWN1_LINK_LOCAL				= TEMPLATE_NS + ".link.down.1.local";
	public static final String	DOWN1_LINK_AUTOBAHN				= TEMPLATE_NS + ".link.down.1.other";
	public static final String	DOWN2_LINK_LOCAL				= TEMPLATE_NS + ".link.down.2.local";
	public static final String	DOWN2_LINK_AUTOBAHN				= TEMPLATE_NS + ".link.down.2.other";

	// if we want to create such virtual links (not required)
	public static final String	INTER_LINK						= TEMPLATE_NS + ".link.inter";
	public static final String	DOWN1_LINK						= TEMPLATE_NS + ".link.down.1";
	public static final String	DOWN2_LINK						= TEMPLATE_NS + ".link.down.2";
	public static final String	UP1_LINK						= TEMPLATE_NS + ".link.up.1";
	public static final String	UP2_LINK						= TEMPLATE_NS + ".link.up.2";

	public static final String	LINK_TYPE_LT					= "lt";
	public static final String	LINK_TYPE_AUTOBAHN				= "autobahn";
	public static final String	LINK_TYPE_ETH					= "eth";
	public static final String	LINK_TYPE_VIRTUAL				= "virtual";

	public static final String	AUTOBAHN						= TEMPLATE_NS + ".autobahn";

}
