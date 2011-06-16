package net.i2cat.mantychore.capability.chassis.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.Activator;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;


import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chasis", name = "listInterfaces", description = "List all the interfaces of a given resource.")
public class ListInterfacesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the interfaces.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("listing resource interfaces");

		try {
			IResourceManager manager = getResourceManager();
			printInfo("Listing interfaces...");

			if (!splitResourceName(resourceId))
				return -1;

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(args[0], args[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);
			if (resource == null) {
				printError("Error in resource.");
				endcommand();
				return -1;
			}
			validateResource(resource);

			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			if (chassisCapability == null) {
				printError("Error in capability.");
				endcommand();
				return -1;
			}
			chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);

			// TODO WE NEED TO USE QUEUE WITH CHASSIS
			IQueueManagerService queue = Activator.getQueueManagerService(resourceIdentifier.getId());

			printInfo("Sending the message...");
			queue.execute();
			ComputerSystem model = (ComputerSystem) resource.getModel();
			printSymbol(horizontalSeparator);
			printInfo(" [Interface name] 	IP/MASK			");
			printSymbol(horizontalSeparator);

			for (Object logicalDevice : model.getLogicalDevices()) {
				// TODO CHECK IF IT IS POSSIBLE

				if (logicalDevice instanceof EthernetPort) {
					EthernetPort ethernetPort = (EthernetPort) logicalDevice;
					for (ProtocolEndpoint protocolEndpoint : ethernetPort.getProtocolEndpoint()) {
						if (protocolEndpoint instanceof IPProtocolEndpoint) {
							String ipv4 = ((IPProtocolEndpoint) protocolEndpoint).getIPv4Address();
							String mask = ((IPProtocolEndpoint) protocolEndpoint).getSubnetMask();
							if (ipv4 != null && mask != null) {
								printInfo(bullet + " [" + ethernetPort.getElementName() + "." + ethernetPort.getPortNumber() + "]  " + ipv4 + " / " + mask);
							} else {
								printInfo(bullet + " [" + ethernetPort.getElementName() + "." + ethernetPort.getPortNumber() + "]  ");
							}
						}
					}
				}
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

	public ICapability getCapability(List<ICapability> capabilities, String type) {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		return null;
	}

	private boolean validateResource(IResource resource) {
		if (resource == null)
			return false;
		if (resource.getModel() == null)
			return false;

		return true;
	}

}
