package org.opennaas.extensions.bod.autobahn.commands;

/*
 * #%L
 * OpenNaaS :: BoD :: Autobahn driver
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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.queue.QueueConstants;

public class Transaction
{
	private static final ThreadLocal<Transaction>	transaction	=
																		new ThreadLocal<Transaction>() {
																			@Override
																			protected Transaction initialValue()
																			{
																				return new Transaction();
																			}
																		};

	/** Commands to execute. */
	private final List<IAutobahnCommand>			queue		= newArrayList();

	/** Commands already executed. */
	private final List<IAutobahnCommand>			log			= newArrayList();

	/** True while a transaction is open. */
	private boolean									open		= false;

	public static Transaction getInstance()
	{
		return transaction.get();
	}

	public void begin()
			throws ActionException
	{
		checkState(!open);
		queue.clear();
		log.clear();
		open = true;
	}

	public ActionResponse commit()
	{
		checkState(open);

		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(QueueConstants.CONFIRM);

		for (IAutobahnCommand command : queue) {
			Response response = command.execute();
			actionResponse.addResponse(response);

			log.add(command);

			if (response.getStatus() != Response.Status.OK) {
				actionResponse.setStatus(ActionResponse.STATUS.ERROR);
				actionResponse.setInformation(getFirst(response.getErrors(),
						"Commit failed"));
				return actionResponse;
			}
		}
		open = false;

		actionResponse.setStatus(ActionResponse.STATUS.OK);
		actionResponse.setInformation("Commit succeeded");
		return actionResponse;
	}

	public ActionResponse rollback()
	{
		checkState(open);
		try {
			ActionResponse actionResponse = new ActionResponse();
			actionResponse.setActionID(QueueConstants.RESTORE);
			actionResponse.setStatus(ActionResponse.STATUS.OK);
			actionResponse.setInformation("Rollback succeeded");

			for (IAutobahnCommand command : log) {
				Response response = command.undo();
				actionResponse.addResponse(response);
				if (response.getStatus() != Response.Status.OK) {
					actionResponse.setStatus(ActionResponse.STATUS.ERROR);
					actionResponse.setInformation(getFirst(response.getErrors(),
							"Rollback failed"));
				}
			}

			return actionResponse;
		} finally {
			open = false;
		}
	}

	public void add(IAutobahnCommand command)
	{
		checkState(open);
		queue.add(command);
	}
}