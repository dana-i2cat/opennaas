package org.opennaas.extensions.genericnetwork.actionsets.internal.portstatistics;

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
import org.opennaas.extensions.genericnetwork.actionsets.internal.portstatistics.actions.GetPortStatisticsAction;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatisticsActionSet;

/**
 * An ActionSet Implementation that delegates to OFSwitches in the network.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public class NetPortStatisticsInternalActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "internal";

	public NetPortStatisticsInternalActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(PortStatisticsActionSet.GET_PORT_STATISTICS, GetPortStatisticsAction.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(PortStatisticsActionSet.GET_PORT_STATISTICS);

		return actionNames;
	}
}
