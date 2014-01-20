package org.opennaas.extensions.router.capability.ospfv3;

/*
 * #%L
 * OpenNaaS :: Router :: OSPFv3 Capability
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

import org.opennaas.core.resources.action.IActionSetDefinition;

public class OSPFv3ActionSet implements IActionSetDefinition {

	public static final String	OSPFv3_CONFIGURE				= "configureOSPFv3";
	public static final String	OSPFv3_CLEAR					= "clearOSPFv3";
	public static final String	OSPFv3_GET_CONFIGURATION		= "getOSPFv3Configuration";
	public static final String	OSPFv3_ACTIVATE					= "activateOSPFv3";
	public static final String	OSPFv3_DEACTIVATE				= "deactivateOSPFv3";
	public static final String	OSPFv3_ENABLE_INTERFACE			= "enableOSPFv3InInterface";
	public static final String	OSPFv3_DISABLE_INTERFACE		= "disableOSPFv3InInterface";
	public static final String	OSPFv3_CONFIGURE_AREA			= "configureOSPFv3Area";
	public static final String	OSPFv3_REMOVE_AREA				= "removeOSPFv3Area";
	public static final String	OSPFv3_ADD_INTERFACE_IN_AREA	= "addOSPv3FInterfaceInArea";
	public static final String	OSPFv3_REMOVE_INTERFACE_IN_AREA	= "removeOSPFv3InterfaceInArea";

}
