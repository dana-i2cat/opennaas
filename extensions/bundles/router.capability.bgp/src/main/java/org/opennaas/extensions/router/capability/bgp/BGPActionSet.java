package org.opennaas.extensions.router.capability.bgp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BGPActionSet {

	public static final String			CONFIGURE_BGP	= "bgpConfigure";
	public static final String			UNCONFIGURE_BGP	= "bgpUnconfigure";

	private static final List<String>	actionIds		= new ArrayList<String>(Arrays.asList(
																CONFIGURE_BGP,
																UNCONFIGURE_BGP));

	public static List<String> getActionIds() {
		return actionIds;
	}

}
