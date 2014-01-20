package org.opennaas.extensions.router.capability.ospf;

/*
 * #%L
 * OpenNaaS :: Router :: OSPF capability
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

public class OSPFActionSet implements IActionSetDefinition {

	public static final String	OSPF_CONFIGURE					= "configureOSPF";
	public static final String	OSPF_CLEAR						= "clearOSPF";
	public static final String	OSPF_GET_CONFIGURATION			= "getOSPFConfiguration";
	public static final String	OSPF_ACTIVATE					= "activateOSPF";
	public static final String	OSPF_DEACTIVATE					= "deactivateOSPF";
	public static final String	OSPF_ENABLE_INTERFACE			= "enableOSPFInInterface";
	public static final String	OSPF_DISABLE_INTERFACE			= "disableOSPFInInterface";
	public static final String	OSPF_CONFIGURE_AREA				= "configureOSPFArea";
	public static final String	OSPF_REMOVE_AREA				= "removeOSPFArea";
	public static final String	OSPF_ADD_INTERFACE_IN_AREA		= "addOSPFInterfaceInArea";
	public static final String	OSPF_REMOVE_INTERFACE_IN_AREA	= "removeOSPFInterfaceInArea";

}
