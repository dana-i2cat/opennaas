package org.opennaas.extensions.vcpe.manager.templates.mp;

public class TemplateConstants {

	/**
	 * Template Name Space. This namespace is meant to be unique in whole OpenNaaS. Particularly, there MUST not exist any other vcpe template using
	 * the same namespace.
	 */
	public static final String	TEMPLATE_NS				= "org.opennaas.extensions.vcpe.manager.templates.mp";

	// ////////////////////
	// PHYSICAL ELEMENTS //
	// ////////////////////

	// LRs
	/**
	 * Physical router hosting LR_1_ROUTER, LR_2_ROUTER and LR_CLIENT_ROUTER
	 */
	public static final String	ROUTER_1_PHY			= TEMPLATE_NS + "vcpe.router.1.phy";

	// Interfaces in ROUTER_1_PHY
	/**
	 * Physical interface in ROUTER_1_PHY connected to WAN1
	 */
	public static final String	ROUTER_1_PHY_IFACE_UP1	= TEMPLATE_NS + "vcpe.router.1.phy.iface.phy.up1";
	/**
	 * Physical interface in ROUTER_1_PHY connected to WAN2
	 */
	public static final String	ROUTER_1_PHY_IFACE_UP2	= TEMPLATE_NS + "vcpe.router.1.phy.iface.phy.up2";
	/**
	 * Physical interface in ROUTER_1_PHY connected to LAN_CLIENT
	 */
	public static final String	ROUTER_1_PHY_IFACE_DOWN	= TEMPLATE_NS + "vcpe.router.1.phy.iface.phy.down";
	/**
	 * Physical loopback interface in ROUTER_1_PHY
	 */
	public static final String	ROUTER_1_PHY_IFACE_LO	= TEMPLATE_NS + "vcpe.router.1.phy.iface.phy.lo";
	/**
	 * Physical logical tunnel interface in ROUTER_1_PHY used to connect LRs between each others. It will host links LINK_LR_1_LR_CLIENT and
	 * LINK_LR_2_LR_CLIENT.
	 */
	public static final String	ROUTER_1_PHY_IFACE_LT	= TEMPLATE_NS + "vcpe.router.1.phy.iface.phy.lt";

	// Links
	// MP template assumes following physical links exists in reality, although not used in the model.
	/**
	 * Physical link connecting WAN1 and ROUTER_1_PHY_IFACE_UP1
	 */
	public static final String	LINK_WAN1_PHY			= TEMPLATE_NS + "link.phy.wan.1";
	/**
	 * Physical link connecting WAN2 and ROUTER_1_PHY_IFACE_UP2
	 */
	public static final String	LINK_WAN2_PHY			= TEMPLATE_NS + "link.phy.wan.2";
	/**
	 * Physical link connecting ROUTER_1_PHY_IFACE_DOWN and LAN_CLIENT
	 */
	public static final String	LINK_LAN_CLIENT_PHY		= TEMPLATE_NS + "link.phy.lan.client";

	// ///////////////////
	// LOGICAL ELEMENTS //
	// ///////////////////

	// Nets
	/**
	 * Network of provider 1
	 */
	public static final String	WAN1					= TEMPLATE_NS + "net.wan.1";
	/**
	 * Network of provider 2
	 */
	public static final String	WAN2					= TEMPLATE_NS + "net.wan.2";
	/**
	 * Client institution LAN
	 */
	public static final String	LAN_CLIENT				= TEMPLATE_NS + "net.lan.client";
	/**
	 * Logical interface in WAN1. It is connected to LR_1_IFACE_UP through LINK_WAN1
	 */
	public static final String	WAN1_IFACE_DOWN			= TEMPLATE_NS + "net.wan.1.iface.logical.down";
	/**
	 * Logical interface in WAN2. It is connected to LR_2_IFACE_UP through LINK_WAN2
	 */
	public static final String	WAN2_IFACE_DOWN			= TEMPLATE_NS + "net.wan.2.iface.logical.down";
	/**
	 * Logical interface LAN_CLIENT. It is connected to LR_CLIENT_IFACE_DOWN through LINK_LAN_CLIENT
	 */
	public static final String	LAN_CLIENT_IFACE_UP		= TEMPLATE_NS + "net.lan.client.iface.logical.up";

	// LRs
	/**
	 * Logical Router hosted by LR_1_PHY_ROUTER. It contains 3 interfaces: - LR_1_IFACE_UP - LR_1_IFACE_DOWN - LR_1_IFACE_LO This LR is connected to
	 * WAN1 (through LR_1_IFACE_UP), and to LR_CLIENT_ROUTER (through LR_1_IFACE_DOWN)
	 */
	public static final String	LR_1_ROUTER				= TEMPLATE_NS + "vcpe.lr.1";
	/**
	 * Logical Router hosted by LR_2_PHY_ROUTER. It contains 3 interfaces: - LR_2_IFACE_UP - LR_2_IFACE_DOWN - LR_2_IFACE_LO This LR is connected to
	 * WAN2 (through LR_2_IFACE_UP), and to LR_CLIENT_ROUTER (through LR_2_IFACE_DOWN)
	 */
	public static final String	LR_2_ROUTER				= TEMPLATE_NS + "vcpe.lr.2";
	/**
	 * Logical Router hosted by LR_CLIENT_PHY_ROUTER. It contains 4 interfaces: - LR_CLIENT_IFACE_UP1 - LR_CLIENT_IFACE_UP2 - LR_CLIENT_IFACE_DOWN -
	 * LR_CLIENT_IFACE_LO This LR is connected to LR_1_ROUTER (through LR_CLIENT_IFACE_UP1), to LR_2_ROUTER (through LR_CLIENT_IFACE_UP2), and to
	 * LAN_CLIENT (through LR_CLIENT_IFACE_DOWN)
	 */
	public static final String	LR_CLIENT_ROUTER		= TEMPLATE_NS + "vcpe.lr.client";

	// LR_1_ROUTER logical interfaces
	/**
	 * Logical interface in LR_1_ROUTER. Hosted by LR_1_PHY_IFACE_UP. It is linked to LINK_WAN1
	 */
	public static final String	LR_1_IFACE_UP			= TEMPLATE_NS + "vcpe.lr.1.iface.logical.up";
	/**
	 * Logical interface in LR_1_ROUTER. Hosted by LR_1_PHY_IFACE_DOWN. It is linked to LINK_LR_1_LR_CLIENT
	 */
	public static final String	LR_1_IFACE_DOWN			= TEMPLATE_NS + "vcpe.lr.1.iface.logical.down";
	/**
	 * Logical loopback interface in LR_1_ROUTER. Hosted by LR_1_PHY_IFACE_LO
	 */
	public static final String	LR_1_IFACE_LO			= TEMPLATE_NS + "vcpe.lr.1.iface.logical.lo";

	// LR_2_ROUTER logical interfaces
	/**
	 * Logical interface in LR_2_ROUTER. Hosted by LR_2_PHY_IFACE_UP. It is linked to LINK_WAN2
	 */
	public static final String	LR_2_IFACE_UP			= TEMPLATE_NS + "vcpe.lr.2.iface.logical.up";
	/**
	 * Logical interface in LR_2_ROUTER. Hosted by LR_2_PHY_IFACE_DOWN. It is linked to LINK_LR_2_LR_CLIENT
	 */
	public static final String	LR_2_IFACE_DOWN			= TEMPLATE_NS + "vcpe.lr.2.iface.logical.down";
	/**
	 * Logical loopback interface in LR_2_ROUTER. Hosted by LR_2_PHY_IFACE_LO
	 */
	public static final String	LR_2_IFACE_LO			= TEMPLATE_NS + "vcpe.lr.2.iface.logical.lo";

	// LR_CLIENT_ROUTER logical interfaces
	/**
	 * Logical interface in LR_CLIENT_ROUTER. Hosted by LR_CLIENT_PHY_IFACE_UP1. It is linked to LINK_LR_1_LR_CLIENT
	 */
	public static final String	LR_CLIENT_IFACE_UP1		= TEMPLATE_NS + "vcpe.lr.client.iface.logical.up1";
	/**
	 * Logical interface in LR_CLIENT_ROUTER. Hosted by LR_CLIENT_PHY_IFACE_UP2. It is linked to LINK_LR_2_LR_CLIENT
	 */
	public static final String	LR_CLIENT_IFACE_UP2		= TEMPLATE_NS + "vcpe.lr.client.iface.logical.up2";
	/**
	 * Logical interface in LR_CLIENT_ROUTER. Hosted by LR_CLIENT_PHY_IFACE_DOWN. It is linked to LINK_LAN_CLIENT
	 */
	public static final String	LR_CLIENT_IFACE_DOWN	= TEMPLATE_NS + "vcpe.lr.client.iface.logical.down";
	/**
	 * Logical loopback interface in LR_CLIENT_ROUTER. Hosted by LR_CLIENT_PHY_IFACE_LO
	 */
	public static final String	LR_CLIENT_IFACE_LO		= TEMPLATE_NS + "vcpe.lr.client.iface.logical.lo";

	// Links

	// Tagged ethernet links (vlan tagged)
	/**
	 * Logical link connecting LR_1_IFACE_UP and WAN1_IFACE_DOWN. Implemented by LINK_WAN1_PHY
	 */
	public static final String	LINK_WAN1				= TEMPLATE_NS + "link.logical.wan.1";
	/**
	 * Logical link connecting LR_2_IFACE_UP and WAN2_IFACE_DOWN. Implemented by LINK_WAN2_PHY
	 */
	public static final String	LINK_WAN2				= TEMPLATE_NS + "link.logical.wan.2";
	/**
	 * Logical link connecting LR_CLIENT_IFACE_DOWN and LAN_CLIENT_IFACE_UP. Implemented by LINK_LAN_CLIENT_PHY
	 */
	public static final String	LINK_LAN_CLIENT			= TEMPLATE_NS + "link.logical.lan.client";

	// intern links of the vCPE between LRs (logical tunnels)
	/**
	 * Logical link connecting LR_1_IFACE_DOWN and LR_CLIENT_IFACE_UP1. Implemented by ROUTER_1_PHY_IFACE_LT
	 */
	public static final String	LINK_LR_1_LR_CLIENT		= TEMPLATE_NS + "link.logial.intern.1";
	/**
	 * Logical link connecting LR_2_IFACE_DOWN and LR_CLIENT_IFACE_UP2. Implemented by ROUTER_1_PHY_IFACE_LT
	 */
	public static final String	LINK_LR_2_LR_CLIENT		= TEMPLATE_NS + "link.logial.intern.2";

}
