package org.opennaas.extensions.router.capability.chassis.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GREService;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.VLANEndpoint;

@Command(scope = "chassis", name = "showInterfaces", description = "List all interfaces of a given resource.")
public class ShowInterfacesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the interfaces.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("show interfaces information");

		try {
			IResourceManager manager = getResourceManager();
			// printInfo("Showing interfaces...");

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

			for (LogicalDevice logicalDevice : model.getLogicalDevices()) {

				if (logicalDevice instanceof NetworkPort) {

					NetworkPort netPort = (NetworkPort) logicalDevice;

					int portNumber = netPort.getPortNumber();
					String ifaceName = logicalDevice.getName() + "." + String.valueOf(portNumber);

					if (ifaceName.length() < 15) {
						int dif = 15 - ifaceName.length();
						for (int i = 0; i < dif; i++)
							ifaceName += " ";
					}
					printSymbolWithoutDoubleLine("INTERFACE: " + ifaceName);

					if (logicalDevice instanceof LogicalTunnelPort)
						printSymbolWithoutDoubleLine(doubleTab + "Peer-Unit: " + ((LogicalTunnelPort) logicalDevice).getPeer_unit());

					if (netPort.getProtocolEndpoint() != null) {

						for (ProtocolEndpoint pE : netPort.getProtocolEndpoint()) {
							if (pE instanceof VLANEndpoint) {
								printSymbolWithoutDoubleLine(doubleTab + "VLAN id: " + Integer
										.toString(((VLANEndpoint) pE).getVlanID()));
							}
						}
						printSymbolWithoutDoubleLine(doubleTab + "STATE: " + netPort.getOperationalStatus());

					}

					if (netPort.getDescription() != null && !netPort.getDescription().equals("")) {
						printSymbolWithoutDoubleLine(doubleTab + "description: " + netPort.getDescription());
					}

					printSymbol("");

				}
			}

			List<GREService> greServiceList = model.getAllHostedServicesByType(new GREService());

			if (!greServiceList.isEmpty()) {
				GREService greService = greServiceList.get(0);
				for (ProtocolEndpoint pE : greService.getProtocolEndpoint()) {

					printSymbolWithoutDoubleLine("GRE INTERFACE: " + pE.getName());

					printSymbolWithoutDoubleLine(doubleTab + "STATE: " + pE.getOperationalStatus());

					if (pE.getDescription() != null && !pE.getDescription().equals(""))
						printSymbolWithoutDoubleLine(doubleTab + "description: " + pE.getDescription());
					printSymbol("");
				}
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
