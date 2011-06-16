package net.i2cat.mantychore.capability.chassis.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.Activator;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;


import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chassis", name = "setInterface", description = "Set a IP in a the interface of a given resource")
public class SetInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to set the interface.", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "interface", description = "The name of the interface to be setted.", required = true, multiValued = false)
	private String	interfaceName;
	@Argument(index = 2, name = "ip", description = "A valid IPv4: x.x.x.x", required = true, multiValued = false)
	private String	ip;

	@Argument(index = 3, name = "mask", description = "A valid MASK IPv4: x.x.x.x", required = true, multiValued = false)
	private String	mask;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("set resource interface");

		try {
			IResourceManager manager = getResourceManager();
			if (manager == null) {
				printError("Error in manager.");
				endcommand();
				return -1;
			}
			printInfo("Setting " + resourceId + " interface: " + interfaceName + " ip: " + ip + " mask: " + mask);

			if (!splitResourceName(resourceId))
				return -1;

			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(args[0], args[1]);
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

			if (!this.validateResource(resource)) {
				printError("The resource was not found");
				endcommand();
				return -1;
			}
			if (!validateIPAddress(ip)) {
				printError("Ip format incorrect. it must be [255..0].[255..0].[255..0].[255..0]");
				endcommand();
				return -1;
			}
			if (!validateIPAddress(mask)) {
				printError("Mask format incorrect. it must be [255..0].[255..0].[255..0].[255..0]");
				endcommand();
				return -1;
			}
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			if (chassisCapability == null) {
				printError("Error in capability.");
				endcommand();
				return -1;
			}

			Object params = newParamsInterfaceEthernet(interfaceName, ip, mask);

			if (params == null) {
				printError("Error preparing parameters.");
				endcommand();
				return -1;
			}
			printInfo("Preparing the message for setting the interface: ");
			if (params instanceof EthernetPort) {
				EthernetPort ethernetPort = (EthernetPort) params;
				for (ProtocolEndpoint protocolEndpoint : ethernetPort.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						String ipv4 = ((IPProtocolEndpoint) protocolEndpoint).getIPv4Address();
						String mask = ((IPProtocolEndpoint) protocolEndpoint).getSubnetMask();

						printInfo("[" + ethernetPort.getElementName() + "." + ethernetPort.getPortNumber() + "]  " + ipv4 + " / " + mask);

					}
				}
			}

			printInfo("Sending the message...");
			chassisCapability.sendMessage(ActionConstants.SETINTERFACE, params);

			// TODO WE NEED TO USE QUEUE WITH CHASSIS
			IQueueManagerService queue = Activator.getQueueManagerService(resourceIdentifier.getId());
			List<ActionResponse> response = queue.execute();
			printInfo("Interface setted successfully");

		} catch (Exception e) {

			printError("Error in setting interface.");
			printError(e);
			endcommand();
			return -1;
		}
		endcommand();
		return null;
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public Object newParamsInterfaceEthernet(String name, String ipName, String mask) {
		EthernetPort eth = new EthernetPort();

		String[] interfaces = null;
		try {
			interfaces = name.split("\\.");
			if (interfaces.length != 2) {
				printError("Error splitting interface");
				return null;
			}
		} catch (Exception e) {
			interfaces[0] = name;
		}
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setElementName(interfaces[0]);
		eth.setPortNumber(Integer.parseInt(interfaces[1]));
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address(ipName);
		ip.setSubnetMask(mask);
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	public ICapability getCapability(List<ICapability> capabilities, String type) throws Exception {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		throw new Exception();
	}

	private boolean validateResource(IResource resource) {
		if (resource == null)
			return false;
		if (resource.getModel() == null)
			return false;

		return true;
	}

	/**
	 * TODO FORMAT TO STRING
	 * 
	 * @param ipAddress
	 * @return
	 */
	public final static boolean validateIPAddress(String ipAddress) {
		String[] parts = ipAddress.split("\\.");

		if (parts.length != 4) {
			return false;
		}

		for (String s : parts) {
			int i = Integer.parseInt(s);

			if ((i < 0) || (i > 255)) {
				return false;
			}
		}

		return true;
	}

}
