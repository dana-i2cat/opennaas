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
import org.opennaas.extensions.router.junos.actionssets.actions.topologydiscovery.GetInterfaceNeighbourAction;
import org.opennaas.extensions.router.junos.actionssets.actions.topologydiscovery.GetLocalInformationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.topologydiscovery.GetNeighboursAction;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class TopologyDiscoveryActionSet extends ActionSet {

	public TopologyDiscoveryActionSet() {
		super.setActionSetId("topologyDiscoveryteActionSet");

		this.putAction(ActionConstants.TOPOLOGY_DISCOVERY_GET_LOCAL_INFORMATION, GetLocalInformationAction.class);
		this.putAction(ActionConstants.TOPOLOGY_DISCOVERY_GET_NEIGHBOURS, GetNeighboursAction.class);
		this.putAction(ActionConstants.TOPOLOGY_DISCOVERY_GET_INTERFACE_NEIGHBOUR, GetInterfaceNeighbourAction.class);

	}

	@Override
	public List<String> getActionNames() {

		List<String> actionNames = new ArrayList<String>();

		actionNames.add(ActionConstants.TOPOLOGY_DISCOVERY_GET_LOCAL_INFORMATION);
		actionNames.add(ActionConstants.TOPOLOGY_DISCOVERY_GET_NEIGHBOURS);
		actionNames.add(ActionConstants.TOPOLOGY_DISCOVERY_GET_INTERFACE_NEIGHBOUR);

		return actionNames;

	}
}
