package net.i2cat.mantychore.capability.ip.shell;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.ip.IPCapability;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "ipv4", name = "setIP", description = "Set a IP in a the interface of a given resource")
public class SetIPv4Command extends GenericKarafCommand {

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

			Object params = validateInterface(interfaceName, ip, mask);
			if (params == null) {
				endcommand();
				return -1;
			}
			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

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
			ICapability ipCapability = getCapability(resource.getCapabilities(), IPCapability.IP);

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

			printInfo("Sending message to the queue");
			ipCapability.sendMessage(ActionConstants.SETIPv4, params);

		} catch (ResourceException e) {
			printError(e);
			endcommand();
			return -1;
		} catch (Exception e) {
			printError("Error setting interface.");
			printError(e);
			endcommand();
			return -1;
		}
		endcommand();
		return null;
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

	private Object validateInterface(String name, String ipName, String mask) {
		String[] interfaces = null;
		try {
			interfaces = name.split("\\.");
			if (interfaces.length != 2) {
				printError("Cannot split the interface");
				return null;
			}
		} catch (Exception e) {
			interfaces[0] = name;
		}

		if (interfaceName.startsWith("lo")) {
			printError("Configuration for Loopback interface not allowed");
			return null;
		} else if (interfaceName.startsWith("lt")) {
			LogicalTunnelPort lt = new LogicalTunnelPort();
			lt.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
			lt.setElementName(interfaces[0]);
			lt.setPortNumber(Integer.parseInt(interfaces[1]));
			IPProtocolEndpoint ip = new IPProtocolEndpoint();
			ip.setIPv4Address(ipName);
			ip.setSubnetMask(mask);
			lt.addProtocolEndpoint(ip);
			return lt;
		} else {
			EthernetPort eth = new EthernetPort();
			eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
			eth.setElementName(interfaces[0]);
			eth.setPortNumber(Integer.parseInt(interfaces[1]));
			IPProtocolEndpoint ip = new IPProtocolEndpoint();
			ip.setIPv4Address(ipName);
			ip.setSubnetMask(mask);
			eth.addProtocolEndpoint(ip);
			return eth;
		}

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
