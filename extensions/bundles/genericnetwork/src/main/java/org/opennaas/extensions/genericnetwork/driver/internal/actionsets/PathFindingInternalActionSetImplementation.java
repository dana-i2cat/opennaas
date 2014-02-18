package org.opennaas.extensions.genericnetwork.driver.internal.actionsets;

/*
 * #%L
 * OpenNaaS :: Generic Network
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
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingActionSet;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.FindPathForRequestAction;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class PathFindingInternalActionSetImplementation extends ActionSet {

	public static final String	ACTIONSET_ID	= "internal";

	public PathFindingInternalActionSetImplementation() {
		super.setActionSetId(ACTIONSET_ID);

		this.putAction(PathFindingActionSet.FIND_PATH_FOR_REQUEST, FindPathForRequestAction.class);
	}

	@Override
	public List<String> getActionNames() {
		List<String> actionNames = new ArrayList<String>();

		actionNames.add(PathFindingActionSet.FIND_PATH_FOR_REQUEST);

		return actionNames;
	}

}
