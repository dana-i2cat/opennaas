package org.opennaas.extensions.router.capability.ip.shell;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "ipv4", name = "list", description = "List all the interfaces of a given resource.")
public class ListInterfacesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the interfaces.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("listing resource interfaces");

		try {
			IResourceManager manager = getResourceManager();
			// printInfo("Listing interfaces...");

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
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			ComputerSystem model = (ComputerSystem) resource.getModel();
			// printSymbol(horizontalSeparator);
			// printSymbol(" [Interface name] 	IP/MASK			");
			// printSymbol(horizontalSeparator);

			// print ifaces & its ip address
			for (LogicalDevice logicalDevice : model.getLogicalDevices()) {
				if (logicalDevice instanceof NetworkPort) {
					NetworkPort port = (NetworkPort) logicalDevice;
					printSymbolWithoutDoubleLine("[" + port.getName() + "." + port.getPortNumber() + "]  ");
					if (port.getDescription() != null && !port.getDescription().equals("")) {
						printSymbolWithoutDoubleLine(doubleTab + "description: " + port.getDescription());
					}
					printSymbol("");
					if (port.getProtocolEndpoint() != null) {
						for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
							if (protocolEndpoint instanceof IPProtocolEndpoint) {
								String ipv4 = ((IPProtocolEndpoint) protocolEndpoint).getIPv4Address();
								String mask = ((IPProtocolEndpoint) protocolEndpoint).getSubnetMask();
								if (ipv4 != null && mask != null) {
									printSymbol(doubleTab + "IP/MASK: " + ipv4 + " / " + mask);
								}
							}

						}

					}

				}
				// printSymbol("");
			}

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
