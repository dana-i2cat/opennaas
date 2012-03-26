package org.opennaas.extensions.router.capability.chassis.shell;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.VLANEndpoint;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "chassis", name = "setEncapsulation", description = "Set an encapsulation in a given interface.")
public class SetEncapsulationCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "subInterface", description = "The interface where to set the VLAN.", required = true, multiValued = false)
	private String	subinterface;

	@Argument(index = 2, name = "vlanid", description = "the VLAN id.", required = false, multiValued = false)
	private int		vlanId	= -1;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("set Encapsulation");

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

			String[] paramsInterface = splitInterfaces(subinterface);

			// FIXME It is necessary to setvlans in loopback if we want configure LRs
			if (isLoopback(paramsInterface[0]))
				throw new Exception("Not allowed VLAN configuration for loopback interface");

			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Could not get resource with name: " + argsRouterName[0] + ":" + argsRouterName[1]);
				printEndCommand();
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);
			checkParams();
			NetworkPort params = prepareParams();

			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			// printInfo("Sending message to the queue");
			chassisCapability.sendMessage(ActionConstants.SETENCAPSULATION, params);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return null;
		} catch (Exception e) {
			printError("Error setting vlan.");
			printError(e);
			printEndCommand();
			return null;
		}

		printEndCommand();
		return null;
	}

	private void checkParams() throws Exception {
		Pattern ltPattern = Pattern.compile("lt-[0-9]/[0-9]/[0-9].[0-9]");
		Matcher matcher = ltPattern.matcher(subinterface);
		if (!matcher.find())
			throw new Exception("the command encapsulation is limited in logical tunnels");
	}

	private boolean isLoopback(String name) {
		return name.startsWith("lo");

	}

	private NetworkPort prepareParams() throws Exception {
		NetworkPort networkPort = null;
		networkPort = new LogicalTunnelPort();

		String[] args = subinterface.split("\\.");
		// params
		networkPort.setName(args[0]);
		networkPort.setPortNumber(Integer.parseInt(args[1]));
		networkPort.setLinkTechnology(LinkTechnology.OTHER);

		if (vlanId != -1) {
			VLANEndpoint vlanEndpoint = new VLANEndpoint();
			vlanEndpoint.setVlanID(vlanId);
			networkPort.addProtocolEndpoint(vlanEndpoint);
		}
		return networkPort;
	}

	public VLANEndpoint getVLANEnpoint(List<ProtocolEndpoint> protocolEndpoints) throws Exception {
		for (ProtocolEndpoint protocolEndpoint : protocolEndpoints) {
			if (protocolEndpoint instanceof VLANEndpoint) {
				return (VLANEndpoint) protocolEndpoint;
			}
		}
		throw new Exception("VLANEnpoint not found");

	}

}
