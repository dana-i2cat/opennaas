package org.opennaas.extensions.router.capability.ip.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;

@Command(scope = "ip", name = "setIP", description = "Set an IP address in a given interface of a resource")
public class SetIPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id, owning the interface", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "interface", description = "The name of the interface to be setted.", required = true, multiValued = false)
	private String	interfaceName;
	@Argument(index = 2, name = "ip", description = "A valid IPv4 or IPv6 address : x.x.x.x/x or x:x:x:x:x:x:x:x/x", required = true, multiValued = false)
	private String	ipAddress;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("set IP address");

		try {
			IResourceManager manager = getResourceManager();
			if (manager == null) {
				printError("Error in manager.");
				printEndCommand();
				return null;
			}

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
				printError("Could not get resource with name: " + argsRouterName[0] + ":" + argsRouterName[1]);
				printEndCommand();
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);
			validateResource(resource);

			NetworkPort networkPort = getNetworkPort();

			if (networkPort == null) {
				printError("Interface " + interfaceName + " not found in model");
				printEndCommand();
				return null;
			}
			IIPCapability ipCapability = (IIPCapability) resource.getCapabilityByInterface(IIPCapability.class);
			try {
				ipCapability.setIP(networkPort, ipAddress);
			} catch (CapabilityException ce) {
				printError("Ip format incorrect. It must match one of the following constraints: ");
				printError("Ipv4: IPv4 Address + Net mask. Example : 144.156.12.1/24");
				printError("Ipv6: IPv6 Address + Prefix Length. Example: A::43A:B41/64");
				printEndCommand();
				return null;

			}

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return null;
		} catch (Exception e) {
			printError("Error setting ip address in an interface.");
			printError(e);
			printEndCommand();
			return null;
		}
		printEndCommand();
		return null;
	}

	/**
	 * Get the NetworkPort
	 * 
	 * @return the networkPort
	 * @throws Exception
	 */
	private NetworkPort getNetworkPort() throws Exception {
		String argsInterface[] = new String[2];
		NetworkPort networkPort = null;

		if (interfaceName.startsWith("lo")) {
			printError("Configuration for Loopback interface not allowed");
			throw new Exception("Configuration for Loopback interface not allowed");
		} else {
			try {
				argsInterface = splitInterfaces(interfaceName);
				String interfaceName = argsInterface[0];
				int port = Integer.parseInt(argsInterface[1]);

				networkPort = new NetworkPort();
				networkPort.setName(interfaceName);
				networkPort.setPortNumber(port);
				networkPort.setLinkTechnology(LinkTechnology.OTHER);

				printInfo("[" + networkPort.getName() + "." + networkPort.getPortNumber() + "]  " + ipAddress);
			} catch (Exception e) {
				throw e;
			}

			return networkPort;
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
			try {
				int i = Integer.parseInt(s);

				if ((i < 0) || (i > 255)) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}

		return true;
	}

}
