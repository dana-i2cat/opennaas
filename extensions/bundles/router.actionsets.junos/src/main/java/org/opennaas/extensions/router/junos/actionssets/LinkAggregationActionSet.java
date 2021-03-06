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
import org.opennaas.extensions.router.junos.actionssets.actions.linkaggregation.CreateAggregatedInterfaceAction;
import org.opennaas.extensions.router.junos.actionssets.actions.linkaggregation.RemoveAggregatedInterfaceAction;

public class LinkAggregationActionSet extends ActionSet {

	public LinkAggregationActionSet() {
		super.setActionSetId("linkAggregationActionSet");
		this.putAction(ActionConstants.GETCONFIG, GetConfigurationAction.class);
		this.putAction(org.opennaas.extensions.router.capability.linkaggregation.LinkAggregationActionSet.CREATE_AGGREGATED_INTERFACE,
				CreateAggregatedInterfaceAction.class);
		this.putAction(org.opennaas.extensions.router.capability.linkaggregation.LinkAggregationActionSet.REMOVE_AGGREGATED_INTERFACE,
				RemoveAggregatedInterfaceAction.class);

		/* add refresh actions */
		this.refreshActions.add(ActionConstants.GETCONFIG);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(ActionConstants.GETCONFIG);
		actionNames.add(org.opennaas.extensions.router.capability.linkaggregation.LinkAggregationActionSet.CREATE_AGGREGATED_INTERFACE);
		actionNames.add(org.opennaas.extensions.router.capability.linkaggregation.LinkAggregationActionSet.REMOVE_AGGREGATED_INTERFACE);

		return actionNames;
	}

}
