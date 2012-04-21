package org.opennaas.extensions.bod.autobahn.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AutobahnCommand implements IAutobahnCommand
{
	protected Log log = LogFactory.getLog(AutobahnCommand.class);
}