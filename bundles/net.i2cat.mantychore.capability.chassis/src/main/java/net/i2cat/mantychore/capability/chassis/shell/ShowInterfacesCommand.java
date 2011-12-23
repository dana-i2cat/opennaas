package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;

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
					printSymbolWithoutDoubleLine("INTERFACE: " + ethernetPort.getName() + "." + ethernetPort.getPortNumber());
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
					printSymbolWithoutDoubleLine("INTERFACE: " + lt.getName() + "." + lt.getPortNumber());
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
