package org.opennaas.core.scripting.shell;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.core.scripting.IScriptingManager;

@Command(scope = "resource", name = "script", description = "Run the mentioned operations script")
public class RunScriptCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "name of the scripts", description = "The names of the scripts to be sequentially run.", required = false, multiValued = true)
	private List<String>	scripts;

	@Option(name = "--list", aliases = { "-l" }, description = "List available scripts.")
	boolean					list			= false;

	@Option(name = "--ignore-errors", aliases = { "-f" }, description = "If set, scripts will be run sequentially even if one fails. (default: false).")
	boolean					ignoreErrors	= false;

	@Override
	protected Object doExecute() throws Exception {

		if (list) {
			throw new NotImplementedException();
		} else if (scripts == null || scripts.size() == 0)
		{
			printError("At least one script should be provided.");
			return null;
		}

		IScriptingManager scriptingManager = Activator.getScriptingManagerService();

		for (String script : scripts) {
			try {
				scriptingManager.runScript(script);

			} catch (Exception e) {
				printError("Error on script " + script + ": " + e.getMessage());

				if (ignoreErrors)
					continue;
				else
					throw e;
			}
		}

		return null;
	}
}
