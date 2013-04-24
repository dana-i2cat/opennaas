package org.opennaas.extensions.router.capability.ip.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;

@Command(scope = "ip", name = "addIP", description = "Add an IP address to a given interface of a resource")
public class AddIPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id, owning the interface", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "interface", description = "The name of the interface to be setted.", required = true, multiValued = false)
	private String	interfaceName;
	@Argument(index = 2, name = "ip", description = "A valid IPv4 or IPv6 address : x.x.x.x/x or x:x:x:x:x:x:x:x/x", required = true, multiValued = false)
	private String	ipAddress;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Add IP address");

		try {
			IResourceManager manager = getResourceManager();

			String[] argsRouterName = splitResourceName(resourceId);
			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

			IResource resource = manager.getResource(resourceIdentifier);
			validateResource(resource);

			NetworkPort networkPort = buildNetworkPort();

			IIPCapability ipCapability = (IIPCapability) resource.getCapabilityByInterface(IIPCapability.class);
			ipCapability.addIP(networkPort, ipAddress);

		} catch (Exception e) {
			printError("Error setting ip address in an interface.");
			printError(e);
			printEndCommand();
			return null;
		}

		printEndCommand();

		return null;
	}

	private NetworkPort buildNetworkPort() throws Exception {
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

}
