package org.opennaas.extensions.router.junos.actionssets;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.GetConfigurationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.ConfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UnconfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPPriorityAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPVirtualIPAddressAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPVirtualLinkAddressAction;

/**
 * @author Julio Carlos Barrera
 * @author Adrian Rosello Rey (i2CAT)
 */
public class VRRPActionSet extends ActionSet {

	public VRRPActionSet() {
		super.setActionSetId("VRRPActionSet");

		this.putAction(ActionConstants.VRRP_CONFIGURE, ConfigureVRRPAction.class);
		this.putAction(ActionConstants.VRRP_UNCONFIGURE, UnconfigureVRRPAction.class);
		this.putAction(ActionConstants.VRRP_UPDATE_IP_ADDRESS, UpdateVRRPVirtualIPAddressAction.class);
		this.putAction(ActionConstants.VRRP_UPDATE_PRIORITY, UpdateVRRPPriorityAction.class);
		this.putAction(ActionConstants.VRRP_UPDATE_VIRTUAL_LINK_ADDRESS, UpdateVRRPVirtualLinkAddressAction.class);
		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.VRRP_CONFIGURE);
		actionNames.add(ActionConstants.VRRP_UNCONFIGURE);
		actionNames.add(ActionConstants.VRRP_UPDATE_IP_ADDRESS);
		actionNames.add(ActionConstants.VRRP_UPDATE_PRIORITY);
		actionNames.add(ActionConstants.VRRP_UPDATE_VIRTUAL_LINK_ADDRESS);

		return actionNames;
	}
}