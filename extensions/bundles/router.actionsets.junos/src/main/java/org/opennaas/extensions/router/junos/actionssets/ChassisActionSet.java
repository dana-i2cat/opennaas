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
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.ConfigureStatusAction;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.ConfigureSubInterfaceAction;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.DeleteSubInterfaceAction;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.RemoveTaggedEthernetEncapsulationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.SetTaggedEthernetEncapsulationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.SetVlanIdAction;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.AddInterfaceToLogicalRouterAction;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.CreateLogicalRouterAction;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.DeleteLogicalRouterAction;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.RemoveInterfaceFromLogicalRouterAction;

@SuppressWarnings("serial")
public class ChassisActionSet extends ActionSet {

	public ChassisActionSet() {
		super.setActionSetId("chassisActionSet");

		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
		this.putAction(ActionConstants.DELETESUBINTERFACE, DeleteSubInterfaceAction.class);
		this.putAction(ActionConstants.CONFIGURESUBINTERFACE, ConfigureSubInterfaceAction.class);
		this.putAction(ActionConstants.CONFIGURESTATUS, ConfigureStatusAction.class);
		this.putAction(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION, SetTaggedEthernetEncapsulationAction.class);
		this.putAction(ActionConstants.REMOVE_TAGGEDETHERNET_ENCAPSULATION, RemoveTaggedEthernetEncapsulationAction.class);
		this.putAction(ActionConstants.SET_VLANID, SetVlanIdAction.class);
		// this.putAction(ActionConstants.SETINTERFACEDESCRIPTION, SetInterfaceDescriptionAction.class);
		this.putAction(ActionConstants.CREATELOGICALROUTER, CreateLogicalRouterAction.class);
		this.putAction(ActionConstants.DELETELOGICALROUTER, DeleteLogicalRouterAction.class);
		this.putAction(ActionConstants.ADDINTERFACETOLOGICALROUTER, AddInterfaceToLogicalRouterAction.class);
		this.putAction(ActionConstants.REMOVEINTERFACEFROMLOGICALROUTER, RemoveInterfaceFromLogicalRouterAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(ActionConstants.CONFIGURESUBINTERFACE);
		actionNames.add(ActionConstants.DELETESUBINTERFACE);
		actionNames.add(ActionConstants.CONFIGURESTATUS);
		actionNames.add(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION);
		actionNames.add(ActionConstants.REMOVE_TAGGEDETHERNET_ENCAPSULATION);
		actionNames.add(ActionConstants.SET_VLANID);
		// actionNames.add(ActionConstants.SETINTERFACEDESCRIPTION);
		actionNames.add(ActionConstants.CREATELOGICALROUTER);
		actionNames.add(ActionConstants.DELETELOGICALROUTER);
		actionNames.add(ActionConstants.ADDINTERFACETOLOGICALROUTER);
		actionNames.add(ActionConstants.REMOVEINTERFACEFROMLOGICALROUTER);

		return actionNames;
	}
}
