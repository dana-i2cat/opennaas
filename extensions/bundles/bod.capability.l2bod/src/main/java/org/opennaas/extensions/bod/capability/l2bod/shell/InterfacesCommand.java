package org.opennaas.extensions.bod.capability.l2bod.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;

import static com.google.common.collect.Iterables.filter;

@Command(scope = "l2bod", name = "interfaces", description = "Show interfaces of BoD resource.")
public class InterfacesCommand extends GenericKarafCommand
{
	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception
	{
		printInitCommand("l2bod:interfaces");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			NetworkModel model = (NetworkModel) resource.getModel();
			for (Interface i: filter(model.getNetworkElements(), Interface.class)) {
				printInfo(i.getName() + " (" + i + ")");
			}
		} catch (Exception e) {
			printError("Error listing interfaces of  " + resourceId);
			printError(e);
		} finally {
			printEndCommand();
		}
		return null;
	}

}