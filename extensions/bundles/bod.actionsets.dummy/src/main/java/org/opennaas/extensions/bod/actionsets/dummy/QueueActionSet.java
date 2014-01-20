package org.opennaas.extensions.bod.actionsets.dummy;

/*
 * #%L
 * OpenNaaS :: BoD :: Dummy ActionSet
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
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.extensions.bod.actionsets.dummy.actions.queue.ConfirmAction;
import org.opennaas.extensions.bod.actionsets.dummy.actions.queue.IsAliveAction;
import org.opennaas.extensions.bod.actionsets.dummy.actions.queue.PrepareAction;
import org.opennaas.extensions.bod.actionsets.dummy.actions.queue.RestoreAction;

public class QueueActionSet extends ActionSet {

	public QueueActionSet() {

		super.setActionSetId("queueActionSet");
		this.putAction(QueueConstants.CONFIRM, ConfirmAction.class);
		this.putAction(QueueConstants.ISALIVE, IsAliveAction.class);
		this.putAction(QueueConstants.PREPARE, PrepareAction.class);
		this.putAction(QueueConstants.RESTORE, RestoreAction.class);
	}

	@Override
	public List<String> getActionNames() {

		List<String> actionNames = new ArrayList<String>();
		actionNames.add(QueueConstants.CONFIRM);
		actionNames.add(QueueConstants.ISALIVE);
		actionNames.add(QueueConstants.PREPARE);
		actionNames.add(QueueConstants.RESTORE);
		return actionNames;
	}
}
