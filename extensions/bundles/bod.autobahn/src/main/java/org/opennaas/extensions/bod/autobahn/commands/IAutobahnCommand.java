package org.opennaas.extensions.bod.autobahn.commands;

import org.opennaas.core.resources.action.ActionException;

public interface IAutobahnCommand
{
	void execute()
		throws ActionException;
	void undo()
		throws ActionException;
}