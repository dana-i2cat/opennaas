package org.opennaas.extensions.bod.autobahn.commands;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennaas.core.resources.command.Command;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;

import static java.util.Arrays.asList;

public abstract class AutobahnCommand
	extends Command
	implements IAutobahnCommand
{
	protected Log log = LogFactory.getLog(AutobahnCommand.class);

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