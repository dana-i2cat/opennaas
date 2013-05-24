package org.opennaas.extensions.pdu.capability.example.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.pdu.capability.example.ExampleCapability;
import org.opennaas.extensions.pdu.capability.example.IExampleCapability;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
@Command(scope = "example", name = "sayHello", description = "It will say hello.")
public class ExampleCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "userName", description = "The name of the person we will greet.", required = true, multiValued = false)
	private String	username;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("sayHello");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			ExampleCapability capab = (ExampleCapability) resource.getCapabilityByInterface(IExampleCapability.class);
			String greeting = capab.sayHello(username);
			printInfo(resourceName + " says : " + greeting);
		} catch (Exception e) {
			printError("Error greeting from resource " + resourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
