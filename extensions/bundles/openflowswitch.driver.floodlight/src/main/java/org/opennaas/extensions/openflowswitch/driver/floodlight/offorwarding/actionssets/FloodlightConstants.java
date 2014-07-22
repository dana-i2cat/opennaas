package org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
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
	public static final String	MIN_PRIORITY				= "0";
	public static final String	MAX_PRIORITY				= "32767";

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
