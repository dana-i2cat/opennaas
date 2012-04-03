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
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.GREService;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
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

			for (Object logicalDevice : model.getLogicalDevices()) {
				// TODO CHECK IF IT IS POSSIBLE
				if (logicalDevice instanceof EthernetPort) {
					EthernetPort ethernetPort = (EthernetPort) logicalDevice;

					int reservedSpace = 15;
					String ifaceName = ethernetPort.getName() + "." + ethernetPort.getPortNumber();
					if (ifaceName.length() < 15) {
						int dif = 15 - ifaceName.length();
						for (int i = 0; i < dif; i++)
							ifaceName += " ";
					}
					printSymbolWithoutDoubleLine("INTERFACE: " + ifaceName);
					if (ethernetPort.getProtocolEndpoint() != null) {
						for (ProtocolEndpoint protocolEndpoint : ethernetPort.getProtocolEndpoint()) {
							if (protocolEndpoint instanceof VLANEndpoint) {
								printSymbolWithoutDoubleLine(doubleTab + "VLAN id: " + Integer
										.toString(((VLANEndpoint) protocolEndpoint).getVlanID()));
							}

						}
						// TODO does STATE only make sense when ProtocolEndpoints != null???
						printSymbolWithoutDoubleLine(doubleTab + "STATE: " + ethernetPort.getOperationalStatus());
					}
					if (ethernetPort.getDescription() != null && !ethernetPort.getDescription().equals("")) {
						printSymbolWithoutDoubleLine(doubleTab + "description: " + ethernetPort.getDescription());
					}

				} else if (logicalDevice instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) logicalDevice;

					int reservedSpace = 15;
					String ifaceName = lt.getName() + "." + lt.getPortNumber();
					if (ifaceName.length() < 15) {
						int dif = 15 - ifaceName.length();
						for (int i = 0; i < dif; i++)
							ifaceName += " ";
					}
					printSymbolWithoutDoubleLine("INTERFACE: " + ifaceName);
					printSymbolWithoutDoubleLine(doubleTab + "Peer-Unit: " + lt.getPeer_unit());
					if (lt.getProtocolEndpoint() != null) {
						for (ProtocolEndpoint protocolEndpoint : lt.getProtocolEndpoint()) {
							if (protocolEndpoint instanceof VLANEndpoint) {
								printSymbolWithoutDoubleLine(doubleTab + "VLAN id: " + Integer
										.toString(((VLANEndpoint) protocolEndpoint).getVlanID()));

							}
						}
						// TODO does STATE only make sense when ProtocolEndpoints != null???
						printSymbolWithoutDoubleLine(doubleTab + "STATE: " + lt.getOperationalStatus());
					}
					if (lt.getDescription() != null && !lt.getDescription().equals("")) {
						printSymbolWithoutDoubleLine("description: " + lt.getDescription());
					}
				}

				printSymbol("");
			}

			List<GREService> greServiceList = model.getAllHostedServicesByType(new GREService());

			if (!greServiceList.isEmpty()) {
				GREService greService = greServiceList.get(0);
				for (ProtocolEndpoint pE : greService.getProtocolEndpoint()) {
					printSymbolWithoutDoubleLine("GRE INTERFACE: " + pE.getName());
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
