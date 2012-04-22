package org.opennaas.extensions.bod.capability.l2bod.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;

import static com.google.common.collect.Iterables.filter;

@Command(scope = "l2bod", name = "links", description = "Show links of BoD resource.")
public class LinksCommand extends GenericKarafCommand
{
	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception
	{
		printInitCommand("l2bod:links");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			NetworkModel model = (NetworkModel) resource.getModel();
			for (Interface i: filter(model.getNetworkElements(), Interface.class)) {
				Link link = i.getLinkTo();
				if (link != null) {
					printInfo(i.getName() + " -> " + link.getSink().getName() + " (" + link + ")");
				}
			}
		} catch (Exception e) {
			printError("Error listing links of  " + resourceId);
			printError(e);
		} finally {
			printEndCommand();
		}
		return null;
	}
}