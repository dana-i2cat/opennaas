package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.CreateOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.GetOFForwardingAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions.RemoveOFForwardingAction;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class RyuOFForwardingActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "ofForwardingActionSetRyu";

	public RyuOFForwardingActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);

		this.putAction(OpenflowForwardingActionSet.GETFLOWS, GetOFForwardingAction.class);
		this.putAction(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE, CreateOFForwardingAction.class);
		this.putAction(OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE, RemoveOFForwardingAction.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE);
		actionNames.add(OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE);
		actionNames.add(OpenflowForwardingActionSet.GETFLOWS);

		return actionNames;
	}

}
