package org.opennaas.extensions.router.capability.ip;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
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

public class IPActionSet implements IActionSetDefinition {

	public static final String	SET_INTERFACE_DESCRIPTION	= "setInterfaceDescription";
	public static final String	SET_IPv4					= "setIPv4";
	public static final String	SET_IPv6					= "setIPv6";
	public static final String	ADD_IPv4					= "addIPv4";
	public static final String	ADD_IPv6					= "addIPv6";
	public static final String	REMOVE_IPv4					= "removeIPv4";
	public static final String	REMOVE_IPv6					= "removeIPv6";
}
