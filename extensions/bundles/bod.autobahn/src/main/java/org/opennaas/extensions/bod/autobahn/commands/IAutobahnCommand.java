package org.opennaas.extensions.bod.autobahn.commands;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.command.Response;

public interface IAutobahnCommand
{
	Response execute();
	Response undo();
}