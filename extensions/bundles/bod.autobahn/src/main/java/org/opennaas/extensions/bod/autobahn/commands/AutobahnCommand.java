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

import static java.util.Arrays.asList;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.command.Command;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;

public abstract class AutobahnCommand
		extends Command
		implements IAutobahnCommand
{
	protected Log	log	= LogFactory.getLog(AutobahnCommand.class);

	protected Response okResponse(String message, String information)
	{
		Response response = new Response();
		response.setCommandName(getCommandId());
		response.setSentMessage(message);
		response.setStatus(Response.Status.OK);
		response.setInformation(information);
		response.setErrors(new Vector<String>());
		return response;
	}

	protected Response errorResponse(String message, String... error)
	{
		Response response = new Response();
		response.setCommandName(getCommandId());
		response.setSentMessage(message);
		response.setStatus(Response.Status.ERROR);
		response.setErrors(new Vector(asList(error)));
		return response;
	}

	@Override
	public void initialize() throws CommandException
	{
	}
}