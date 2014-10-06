package org.opennaas.extensions.openflowswitch.driver.floodlight.controllerinformation.actionssets;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
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
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.ControllerInformationActionSet;
import org.opennaas.extensions.openflowswitch.driver.floodlight.controllerinformation.actions.GetHealthStateAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.controllerinformation.actions.GetMemoryUsageAction;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ControllerInformationActionsetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "controllerInformationActionsetFloodlight";

	public ControllerInformationActionsetImplementation() {
		super.setActionSetId(ACTIONSET_ID);
		this.putAction(ControllerInformationActionSet.GET_HEALTH_STATE, GetHealthStateAction.class);
		this.putAction(ControllerInformationActionSet.GET_MEMORY_USAGE, GetMemoryUsageAction.class);

	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(ControllerInformationActionSet.GET_HEALTH_STATE);
		actionNames.add(ControllerInformationActionSet.GET_MEMORY_USAGE);

		return actionNames;
	}

}
