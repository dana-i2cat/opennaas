package org.opennaas.extensions.macbridge.ios.resource.actionssets;

/*
 * #%L
 * OpenNaaS :: MAC Bridge :: IOS Resource
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

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.AddStaticVLANRegistrationAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.CreateVLANConfigurationAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.DeleteStaticVLANRegistrationAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.DeleteVLANConfigurationAction;
import org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge.RefreshAction;

public class VLANAwareBridgeActionSet extends ActionSet {

	public VLANAwareBridgeActionSet() {
		super.setActionSetId("VLANAwareBridgeActionSet");

		this.putAction(org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.CREATE_VLAN_CONFIGURATION,
				CreateVLANConfigurationAction.class);
		this.putAction(org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.DELETE_VLAN_CONFIGURATION,
				DeleteVLANConfigurationAction.class);
		this.putAction(org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.ADD_STATIC_VLAN_REGISTRATION,
				AddStaticVLANRegistrationAction.class);
		this.putAction(
				org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeActionSet.DELETE_STATIC_VLAN_REGISTRATION,
				DeleteStaticVLANRegistrationAction.class);
		this.putAction(RefreshAction.REFRESH_ACTION, RefreshAction.class);

		/* TODO add refresh actions */
		this.refreshActions.add(RefreshAction.REFRESH_ACTION);
	}
}