package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.model.PowerNetModel;

@Command(scope = "gim", name = "show", description = "Shows GIM model")
public class ShowCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("showGIM");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			printPowerNetModel((PowerNetModel) resource.getModel());
		} catch (Exception e) {
			printError("Error in showGIM for resource " + resourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

	private void printPowerNetModel(PowerNetModel model) {
		printSymbol(model.toString());
	}

}
