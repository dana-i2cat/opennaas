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
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.AddOSPFv3InterfaceInAreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ClearOSPFv3Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3Action;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3AreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3InterfaceStatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.ConfigureOSPFv3StatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.GetOSPFv3ConfigAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.RemoveOSPFv3AreaAction;
import org.opennaas.extensions.router.junos.actionssets.actions.ospfv3.RemoveOSPFv3InterfaceInAreaAction;

public class OSPFv3ActionSet extends ActionSet {

	public OSPFv3ActionSet() {
		super.setActionSetId("OSPFv3ActionSet");

		this.putAction(ActionConstants.OSPFv3_GET_CONFIGURATION, GetOSPFv3ConfigAction.class);
		this.putAction(ActionConstants.OSPFv3_CONFIGURE, ConfigureOSPFv3Action.class);
		this.putAction(ActionConstants.OSPFv3_CLEAR, ClearOSPFv3Action.class);
		this.putAction(ActionConstants.OSPFv3_ACTIVATE, ConfigureOSPFv3StatusAction.class);
		this.putAction(ActionConstants.OSPFv3_DEACTIVATE, ConfigureOSPFv3StatusAction.class);
		this.putAction(ActionConstants.OSPFv3_ENABLE_INTERFACE, ConfigureOSPFv3InterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPFv3_DISABLE_INTERFACE, ConfigureOSPFv3InterfaceStatusAction.class);
		this.putAction(ActionConstants.OSPFv3_CONFIGURE_AREA, ConfigureOSPFv3AreaAction.class);
		this.putAction(ActionConstants.OSPFv3_REMOVE_AREA, RemoveOSPFv3AreaAction.class);
		this.putAction(ActionConstants.OSPFv3_ADD_INTERFACE_IN_AREA, AddOSPFv3InterfaceInAreaAction.class);
		this.putAction(ActionConstants.OSPFv3_REMOVE_INTERFACE_IN_AREA, RemoveOSPFv3InterfaceInAreaAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.OSPFv3_GET_CONFIGURATION);

	}
}
