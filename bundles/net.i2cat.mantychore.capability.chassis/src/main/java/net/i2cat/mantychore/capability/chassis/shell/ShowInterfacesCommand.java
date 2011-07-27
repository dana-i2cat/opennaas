package net.i2cat.mantychore.capability.chassis.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.Activator;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;
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

@Command(scope = "chassis", name = "showInterfaces", description = "List all the interfaces of a given resource.")
public class ShowInterfacesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the interfaces.", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--refresh", aliases = { "-r" }, description = "Force to refresh the model with the last router configuration")
	boolean			refresh;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("show resource interfaces information");

		try {
			IResourceManager manager = getResourceManager();
			printInfo("Showing interfaces...");

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
				ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);

				chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);

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

			for (Object logicalDevice : model.getLogicalDevices()) {
				// TODO CHECK IF IT IS POSSIBLE
				if (logicalDevice instanceof EthernetPort) {
					EthernetPort ethernetPort = (EthernetPort) logicalDevice;
					printSymbol(bullet + " INTERFACE: " + ethernetPort.getElementName() + "." + ethernetPort.getPortNumber());
					if (ethernetPort.getProtocolEndpoint() != null) {
						for (ProtocolEndpoint protocolEndpoint : ethernetPort.getProtocolEndpoint()) {
							if (protocolEndpoint instanceof VLANEndpoint) {
								printSymbolWithoutDoubleLine(doubleTab + "VLAN id: " + Integer
										.toString(((VLANEndpoint) protocolEndpoint).getVlanID()));
							}

						}
						printSymbol(doubleTab + "STATE: " + ethernetPort.getOperationalStatus());
					}

				} else if (logicalDevice instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) logicalDevice;
					printSymbolWithoutDoubleLine(bullet + " INTERFACE: " + lt.getElementName() + "." + lt.getPortNumber());
					printSymbolWithoutDoubleLine(doubleTab + "Peer-Unit: " + lt.getPeer_unit());
					if (lt.getProtocolEndpoint() != null) {
						for (ProtocolEndpoint protocolEndpoint : lt.getProtocolEndpoint()) {
							if (protocolEndpoint instanceof VLANEndpoint) {
								printSymbolWithoutDoubleLine(doubleTab + "VLAN id: " + Integer
										.toString(((VLANEndpoint) protocolEndpoint).getVlanID()));

							}
						}
						printSymbol(doubleTab + "STATE: " + lt.getOperationalStatus());
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

	public ICapability getCapability(List<ICapability> capabilities, String type) throws Exception {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		throw new Exception("Error getting the capability");
	}

	private boolean validateResource(IResource resource) throws ResourceException {
		if (resource == null)
			throw new ResourceException("No resource found.");
		if (resource.getModel() == null)
			throw new ResourceException("The resource didn't have a model initialized. Start the resource first.");
		if (resource.getCapabilities() == null) {
			throw new ResourceException("The resource didn't have the capabilities initialized. Start the resource first.");
		}
		return true;
	}

}
