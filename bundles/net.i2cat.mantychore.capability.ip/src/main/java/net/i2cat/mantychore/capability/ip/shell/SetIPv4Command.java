package net.i2cat.mantychore.capability.ip.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.ip.IPCapability;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.NetworkPort.LinkTechnology;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "ipv4", name = "setIP", description = "Set a IP in a given interface of a resource")
public class SetIPv4Command extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to set the interface.", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "interface", description = "The name of the interface to be setted.", required = true, multiValued = false)
	private String	interfaceName;
	@Argument(index = 2, name = "ip", description = "A valid IPv4: x.x.x.x", required = true, multiValued = false)
	private String	ipv4;

	@Argument(index = 3, name = "mask", description = "A valid MASK IPv4: x.x.x.x", required = true, multiValued = false)
	private String	mask;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("set resource interface");

		try {
			IResourceManager manager = getResourceManager();
			if (manager == null) {
				printError("Error in manager.");
				printEndCommand();
				return null;
			}
			printInfo("Setting " + resourceId + " interface: " + interfaceName + " ip: " + ipv4 + " mask: " + mask);

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				printEndCommand();
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);
			printInfo("Preparing the message for setting the interface: ");
			Object params = validateParams(resource);

			if (params == null) {
				printEndCommand();
				return null;
			}

			/* check if it uses logical router */

			if (!validateIPAddress(ipv4)) {
				printError("Ip format incorrect. it must be [255..0].[255..0].[255..0].[255..0]");
				printEndCommand();
				return null;
			}
			if (!validateIPAddress(mask)) {
				printError("Mask format incorrect. it must be [255..0].[255..0].[255..0].[255..0]");
				printEndCommand();
				return null;
			}
			ICapability ipCapability = getCapability(resource.getCapabilities(), IPCapability.IPv4);

			printInfo("Sending message to the queue");
			ipCapability.sendMessage(ActionConstants.SETIPv4, params);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return null;
		} catch (Exception e) {
			printError("Error setting interface.");
			printError(e);
			printEndCommand();
			return null;
		}
		printEndCommand();
		return null;
	}

	private LogicalDevice validateParams(IResource resource) throws Exception {
		String argsInterface[] = new String[2];
		try {
			argsInterface = splitInterfaces(interfaceName);
		} catch (Exception e) {
			return null;
		}

		String name = argsInterface[0];

		if (name.startsWith("lo")) {
			printError("Configuration for Loopback interface not allowed");
			return null;
		}

		int port = Integer.parseInt(argsInterface[1]);
		ComputerSystem routerModel = (ComputerSystem) resource.getModel();
		List<LogicalDevice> ld = routerModel.getLogicalDevices();
		for (LogicalDevice device : ld) {
			// the interface is found on the model
			if (device.getName().equalsIgnoreCase(name)) {
				if (device instanceof NetworkPort) {
					// TODO implement method clone
					if (((NetworkPort) device).getPortNumber() == port) {
						if (device.getName().startsWith("lt")) {
							LogicalTunnelPort lt = new LogicalTunnelPort();
							LogicalTunnelPort ltOld = (LogicalTunnelPort) device;
							lt.setName(ltOld.getName());
							lt.setPortNumber(ltOld.getPortNumber());
							lt.setPeer_unit(ltOld.getPeer_unit());
							lt.setLinkTechnology(LinkTechnology.OTHER);
							IPProtocolEndpoint ip = new IPProtocolEndpoint();
							ip.setIPv4Address(ipv4);
							ip.setSubnetMask(mask);
							lt.addProtocolEndpoint(ip);
							printInfo("[" + lt.getName() + "." + lt.getPortNumber() + "]  " + ipv4 + " / " + mask);
							return lt;
						} else {
							EthernetPort ethOld = (EthernetPort) device;
							EthernetPort eth = new EthernetPort();
							eth.setName(ethOld.getName());
							eth.setPortNumber(ethOld.getPortNumber());
							eth.setLinkTechnology(LinkTechnology.OTHER);
							IPProtocolEndpoint ip = new IPProtocolEndpoint();
							ip.setIPv4Address(ipv4);
							ip.setSubnetMask(mask);
							eth.addProtocolEndpoint(ip);
							printInfo("[" + eth.getName() + "." + eth.getPortNumber() + "]  " + ipv4 + " / " + mask);

							
							return eth;
						}

					}
				}
			}
		}
		return null;

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
