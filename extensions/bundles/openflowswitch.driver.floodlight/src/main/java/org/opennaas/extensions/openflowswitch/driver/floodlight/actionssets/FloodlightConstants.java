package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets;

/**
 * Floodlight constants
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class FloodlightConstants {

	/* flow default values */
	public static final String	DEFAULT_PRIORITY			= "32767";

	/* flow match default values */
	public static final String	DEFAULT_MATCH_WILDCARDS		= "4194303";
	public static final String	DEFAULT_MATCH_INGRESS_PORT	= "0";
	public static final String	DEFAULT_MATCH_SRC_MAC		= "00:00:00:00:00:00";
	public static final String	DEFAULT_MATCH_DST_MAC		= "00:00:00:00:00:00";
	public static final String	DEFAULT_MATCH_VLAN_ID		= "-1";
	public static final String	DEFAULT_MATCH_VLAN_PRIORITY	= "0";
	public static final String	DEFAULT_MATCH_ETHER_TYPE	= "0x0000";
	public static final String	DEFAULT_MATCH_TOS_BITS		= "0";
	public static final String	DEFAULT_MATCH_PROTOCOL		= "0";
	public static final String	DEFAULT_MATCH_SRC_IP		= "0.0.0.0";
	public static final String	DEFAULT_MATCH_DST_IP		= "0.0.0.0";
	public static final String	DEFAULT_MATCH_SRC_PORT		= "0";
	public static final String	DEFAULT_MATCH_DST_PORT		= "0";

}
