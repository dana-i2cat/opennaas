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

import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.extensions.router.junos.actionssets.actions.GetConfigurationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vlanbridge.CreateBridgeDomainAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vlanbridge.DeleteBridgeDomainAction;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 */
public class VLANBridgeActionSet extends ActionSet {

	public static final String	ACTIONSET_ID	= "vlanBridgeActionSet";

	public VLANBridgeActionSet() {

		super.setActionSetId(ACTIONSET_ID);

		this.putAction(ActionConstants.VLAN_BRIDGE_CREATE_BRIDGE_DOMAIN, CreateBridgeDomainAction.class);
		this.putAction(ActionConstants.VLAN_BRIDGE_REMOVE_BRIDGE_DOMAIN, DeleteBridgeDomainAction.class);

		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
	}
	// TODO define actions and refreshActions for this ActionSet.

}
