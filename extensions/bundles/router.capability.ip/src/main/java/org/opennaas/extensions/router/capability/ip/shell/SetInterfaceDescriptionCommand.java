package org.opennaas.extensions.router.capability.ip.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalPort;

@Command(scope = "ipv4", name = "setInterfaceDescription", description = "Sets given description to given interface.")
public class SetInterfaceDescriptionCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interface", description = "The interface whose description is set.", required = true, multiValued = false)
	private String	subinterface;

	@Argument(index = 2, name = "description", description = "Description of the interface.", required = true, multiValued = false)
	private String	description	= "";

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("set interface description");

		try {
			IResourceManager manager = getResourceManager();

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Could not get resource with name: " + argsRouterName[0] + ":" + argsRouterName[1]);
				printEndCommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			IIPCapability ipCapability = (IIPCapability) resource.getCapabilityByInterface(IIPCapability.class);
			ipCapability.setInterfaceDescription(prepareParams());
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error setting description");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	private LogicalPort prepareParams() {
		String[] args = subinterface.split("\\.");

		LogicalPort port;
		if (args.length > 1) {
			// it's a subinterface
			EthernetPort eth = new EthernetPort();
			eth.setName(args[0]);
			eth.setPortNumber(Integer.parseInt(args[1]));
			eth.setDescription(description);
			port = eth;
		} else {
			port = new LogicalPort();
			port.setName(args[0]);
			port.setDescription(description);
		}

		return port;
	}
}
