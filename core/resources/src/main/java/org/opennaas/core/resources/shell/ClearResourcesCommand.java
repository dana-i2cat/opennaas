package org.opennaas.core.resources.shell;

import org.apache.felix.gogo.commands.Command;

@Command(scope = "resource", name = "clear", description = "Remove all resources in the platform (stopping active ones)")
public class ClearResourcesCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("clear resources");
		try {
			getResourceManager().destroyAllResources();
		} catch (Exception e) {
			printError("Error clearing resources");
			printError(e);
		}
		printEndCommand();
		return null;
	}
}
