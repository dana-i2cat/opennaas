package org.opennaas.extensions.genericnetwork.actionsets.internal.ofprovision;

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
import org.opennaas.extensions.genericnetwork.actionsets.internal.ofprovision.actions.AllocateFlowAction;
import org.opennaas.extensions.genericnetwork.actionsets.internal.ofprovision.actions.DeallocateFlowAction;
import org.opennaas.extensions.genericnetwork.actionsets.internal.ofprovision.actions.GetAllocatedFlowsAction;
import org.opennaas.extensions.genericnetwork.capability.ofprovision.OFProvisioningNetworkActionSet;

/**
 * An ActionSet Implementation that delegates to OFSwitches in the network.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public class OFProvisioningInternalActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "internal";

	public OFProvisioningInternalActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(OFProvisioningNetworkActionSet.ALLOCATEFLOW, AllocateFlowAction.class);
		this.putAction(OFProvisioningNetworkActionSet.DEALLOCATEFLOW, DeallocateFlowAction.class);
		this.putAction(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS, GetAllocatedFlowsAction.class);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(OFProvisioningNetworkActionSet.ALLOCATEFLOW);
		actionNames.add(OFProvisioningNetworkActionSet.DEALLOCATEFLOW);
		actionNames.add(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);

		return actionNames;
	}
}
