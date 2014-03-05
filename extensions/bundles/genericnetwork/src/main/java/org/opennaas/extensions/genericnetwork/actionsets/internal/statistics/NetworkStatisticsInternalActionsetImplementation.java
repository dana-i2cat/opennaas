package org.opennaas.extensions.genericnetwork.actionsets.internal.statistics;

/*
 * #%L
 * OpenNaaS :: OF Network
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
import org.opennaas.extensions.genericnetwork.actionsets.internal.statistics.actions.GetNetworkStatisticsAction;
import org.opennaas.extensions.genericnetwork.capability.statistics.NetworkStatisticsActionSet;

/**
 * An ActionSet Implementation that delegates to OFSwitches in the network.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public class NetworkStatisticsInternalActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "internal";

	public NetworkStatisticsInternalActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);

		this.putAction(NetworkStatisticsActionSet.GET_NETWORK_STATISTICS, GetNetworkStatisticsAction.class);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(NetworkStatisticsActionSet.GET_NETWORK_STATISTICS);

		return actionNames;
	}
}
