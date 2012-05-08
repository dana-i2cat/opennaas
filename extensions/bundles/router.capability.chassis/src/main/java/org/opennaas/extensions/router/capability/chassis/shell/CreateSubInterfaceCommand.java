package org.opennaas.extensions.router.capability.chassis.shell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.VLANEndpoint;

@Command(scope = "chassis", name = "createSubInterface", description = "Create a subinterface on a given resource.")
public class CreateSubInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "subInterface", description = "The interface to be created.", required = true, multiValued = false)
	private String	subinterface;

	@Option(name = "--description", aliases = { "-d" }, description = "interface description .")
	private String	description	= "";

	@Option(name = "--vlanid", aliases = { "-v" }, description = "specify vlan id to use vlan-tagging. IMPORTANT, ethernet interfaces need to include vlans")
	private int		vlanid		= -1;

	@Option(name = "--peerunit", aliases = { "-pu" }, description = "specify peer unit for lt interfaces.")
	private int		peerunit	= -1;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("create subInterface");

		try {
			IResourceManager manager = getResourceManager();

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
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			checkParams();

			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);
			chassisCapability.createSubInterface(prepareParams());

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring interfaces.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	public void checkParams() throws Exception {

		Pattern ltPattern = Pattern.compile("lt-[0-9]/[0-9]/[0-9].[0-9]");
		Matcher ltmatcher = ltPattern.matcher(subinterface);
		Pattern ethPattern = Pattern.compile("[fg]e-[0-9]/[0-9]/[0-9].[0-9]");
		Matcher ethmatcher = ethPattern.matcher(subinterface);
		Pattern loPattern = Pattern.compile("lo[0-9].[0-9]");
		Matcher lomatcher = loPattern.matcher(subinterface);

		String[] args = subinterface.split("\\.");
		/* check logical tunnels */
		if (ltmatcher.find()) {
			if (peerunit == -1)
				throw new Exception("peerUnit must be specified in lt interfaces");
		}

		/* check ethernet ports */
		if (ethmatcher.find() || lomatcher.find()) {
			if (vlanid == -1)
				throw new Exception("vlan must be specified in ethernet interfaces");
		}

	}

	private NetworkPort prepareParams() throws Exception {
		String[] args = subinterface.split("\\.");
		// check if it is a logical tunnel
		NetworkPort networkPort = null;
		if (args[0].startsWith("lt")) {
			LogicalTunnelPort logicalTunnel = new LogicalTunnelPort();
			logicalTunnel.setLinkTechnology(LinkTechnology.OTHER);
			// TODO THIS CHECK HAVE TO BE INCLUDED IN THE VIEW?
			if (peerunit == -1)
				throw new Exception("peerUnit must be specified in lt interfaces");
			logicalTunnel.setPeer_unit(peerunit);
			networkPort = logicalTunnel;
		} else {
			networkPort = new EthernetPort();
		}

		networkPort.setName(args[0]);
		networkPort.setPortNumber(Integer.parseInt(args[1]));
		if (vlanid != -1) {
			VLANEndpoint vlanEndpoint = new VLANEndpoint();
			vlanEndpoint.setVlanID(vlanid); // TODO COMPLETE OTHER CASES... INITIALIZE THE VLAN ID TO 1
			networkPort.addProtocolEndpoint(vlanEndpoint);
		}
		networkPort.setDescription(description);
		return networkPort;
	}
}
