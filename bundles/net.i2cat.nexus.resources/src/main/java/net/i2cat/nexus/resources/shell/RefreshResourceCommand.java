package net.i2cat.nexus.resources.shell;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "resource", name = "refresh", description = "Update the data model of a given resource")
public class RefreshResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = false)
	private String	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		initcommand("create resource");

		IResourceManager manager = getResourceManager();

		if (!splitResourceName(resourceIDs))
			return null;

		IResource resource = null;
		IResourceIdentifier identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

		if (identifier == null) {
			printError("Error in identifier.");
			endcommand();
			return null;
		}

		resource = manager.getResource(identifier);
		validateResource(resource);

		// call the method to refresh each capability on resource
		ICapability queueCapab = null;
		for (ICapability capab : resource.getCapabilities()) {
			if (capab instanceof AbstractCapability) {
				if (capab.getCapabilityInformation().getType().equalsIgnoreCase("queue")) {
					queueCapab = capab;
				} else {
					Response response = ((AbstractCapability) capab).sendStartUpActions();
					if (!response.getStatus().equals(Status.OK)) {
						throw new ResourceException();
					}
				}
			}
		}

		endcommand();
		return null;
	}

}
