package net.i2cat.mantychore.capability.ip.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.ip.Activator;
import net.i2cat.mantychore.capability.ip.IPCapability;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

@Command(scope = "ipv4", name = "list", description = "List all the interfaces of a given resource.")
public class ListInterfacesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the interfaces.", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--refresh", aliases = { "-r" }, description = "Force to refresh the model with the router configuration")
	boolean			refresh;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("listing resource interfaces");

		try {
			IResourceManager manager = getResourceManager();
			printInfo("Listing interfaces...");

			if (!splitResourceName(resourceId))
				return null;

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			if (refresh) {
				ICapability ipCapability = getCapability(resource.getCapabilities(), IPCapability.IPv4);

				ipCapability.sendMessage(ActionConstants.GETCONFIG, null);
				// TODO WE NEED TO USE QUEUE WITH CHASSIS
				IQueueManagerService queue = Activator.getQueueManagerService(resourceIdentifier.getId());

				printInfo("Sending the message...");
				try {
					queue.execute();
				} catch (CapabilityException e) {
					queue.empty();
					throw e;
				}
			}

			ComputerSystem model = (ComputerSystem) resource.getModel();
			printSymbol(horizontalSeparator);
			printSymbol(" [Interface name] 	IP/MASK			");
			printSymbol(horizontalSeparator);

			for (LogicalDevice logicalDevice : model.getLogicalDevices()) {
				// TODO CHECK IF IT IS POSSIBLE
				if (logicalDevice instanceof EthernetPort) {
					EthernetPort ethernetPort = (EthernetPort) logicalDevice;
					printSymbolWithoutDoubleLine(bullet + " [" + ethernetPort.getElementName() + "." + ethernetPort.getPortNumber() + "]  ");

					if (ethernetPort.getProtocolEndpoint() != null) {
						for (ProtocolEndpoint protocolEndpoint : ethernetPort.getProtocolEndpoint()) {
							if (protocolEndpoint instanceof IPProtocolEndpoint) {
								String ipv4 = ((IPProtocolEndpoint) protocolEndpoint).getIPv4Address();
								String mask = ((IPProtocolEndpoint) protocolEndpoint).getSubnetMask();
								if (ipv4 != null && mask != null) {
									printSymbol(doubleTab + ipv4 + " / " + mask);
								}
							}

						}
					}

				} else if (logicalDevice instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) logicalDevice;
					printSymbolWithoutDoubleLine(bullet + " [" + lt.getElementName() + "." + lt.getPortNumber() + "]  ");

					if (lt.getProtocolEndpoint() != null) {
						for (ProtocolEndpoint protocolEndpoint : lt.getProtocolEndpoint()) {
							if (protocolEndpoint instanceof IPProtocolEndpoint) {
								String ipv4 = ((IPProtocolEndpoint) protocolEndpoint).getIPv4Address();
								String mask = ((IPProtocolEndpoint) protocolEndpoint).getSubnetMask();
								if (ipv4 != null && mask != null) {
									printSymbol(doubleTab + ipv4 + " / " + mask);
								}
							}
						}
					}

				}
				printSymbol("");
			}

		} catch (ResourceException e) {
			printError(e);
			endcommand();
			return -1;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			endcommand();
			return -1;
		}
		endcommand();
		return null;
	}

	public ICapability getCapability(List<ICapability> capabilities, String type) throws Exception {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		throw new Exception("Error getting the capability");
	}
}
