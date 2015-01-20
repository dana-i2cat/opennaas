package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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
 * Ryu constants
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class RyuConstants {

	/* flow default values */
	public static final String	DEFAULT_PRIORITY			= "32768";
	public static final String	PRIORITY_MIN				= "0";
	public static final String	PRIORITY_MAX				= "32768";

	public static final String	DEFAULT_COOKIE				= "0";
	public static final String	DEFAULT_IDLE_TIMEOUT		= "0";
	public static final String	DEFAULT_HARD_TIMEOUT		= "0";
	public static final String	DEFAULT_TABLE_ID			= "0";

	/* flow match default values */
	public static final String	DEFAULT_MATCH_WILDCARDS		= "4194303";
	public static final String	DEFAULT_MATCH_INGRESS_PORT	= "0";
	public static final String	DEFAULT_MATCH_SRC_MAC		= "00:00:00:00:00:00";
	public static final String	DEFAULT_MATCH_DST_MAC		= "00:00:00:00:00:00";
	public static final String	DEFAULT_MATCH_VLAN_ID		= "0";
	public static final String	DEFAULT_MATCH_VLAN_PRIORITY	= "0";
	public static final String	DEFAULT_MATCH_ETHER_TYPE	= "0";
	public static final String	DEFAULT_MATCH_PROTOCOL		= "0";
	public static final String	DEFAULT_MATCH_SRC_IP		= "0.0.0.0";
	public static final String	DEFAULT_MATCH_DST_IP		= "0.0.0.0";
	public static final String	DEFAULT_MATCH_SRC_PORT		= "0";
	public static final String	DEFAULT_MATCH_DST_PORT		= "0";

}
