package org.opennaas.extensions.router.capability.vrrp;

/*
 * #%L
 * OpenNaaS :: Router :: VRRP Capability
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

/**
 * @author Julio Carlos Barrera
 */
public class VRRPActionSet implements IActionSetDefinition {
	public static final String	VRRP_CONFIGURE						= "configureVRRP";
	public static final String	VRRP_UNCONFIGURE					= "unconfigureVRRP";
	public static final String	VRRP_UPDATE_VIRTUAL_IP_ADDRESS		= "updateVRRPVirtualIPAddress";
	public static final String	VRRP_UPDATE_PRIORITY				= "updateVRRPPriority";
	public static final String	VRRP_UPDATE_VIRTUAL_LINK_ADDRESS	= "updateVRRPVirtualLinkAddress";

}