package org.opennaas.extensions.sampleresource.capability.example.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.sampleresource.capability.example.ExampleCapability;

@Command(scope = "example", name = "sayHello", description = "It will say hello.")
public class ExampleCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "userName", description = "The name of the person we will greet.", required = true, multiValued = false)
	private String	username;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("sayHello");
		try {

			ResourceManager rm = (ResourceManager) getResourceManager();
			IResource resource = rm.getResourceById(resourceId);
			// IResource resource = getResourceFromFriendlyName(resourceId);
			ExampleCapability capab = (ExampleCapability) resource.getCapabilityByType("example");
			String greeting = capab.sayHello(username);
			String resourceName = resource.getResourceDescriptor().getInformation().getName();
			printInfo(greeting + "from the resource" + resourceName);
		} catch (Exception e) {
			printError("Error greeting from resource " + resourceId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
