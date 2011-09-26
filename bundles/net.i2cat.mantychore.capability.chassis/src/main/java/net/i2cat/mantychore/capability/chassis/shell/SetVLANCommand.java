package net.i2cat.mantychore.capability.chassis.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.NetworkPort.LinkTechnology;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chassis", name = "setVLAN", description = "Set a VLAN id in a given interface.")
public class SetVLANCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "subInterface", description = "The interface where to set the VLAN.", required = true, multiValued = false)
	private String	subinterface;

	@Argument(index = 2, name = "VLANid", description = "the VLAN id.", required = true, multiValued = false)
	private int		vlanId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("set VLAN");

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
			if (isLoopback(paramsInterface[1]))
				throw new Exception("Not allowed VLAN configuration for loopback interface");

			IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				printEndCommand();
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			ComputerSystem routerModel = ((ComputerSystem) resource.getModel());
			int pos = containsInterface(paramsInterface[0], routerModel);
			if (pos == -1)
				throw new Exception("The Physical router don't have the interface");

			LogicalDevice logicalDevice = routerModel.getLogicalDevices().get(pos);
			if (!(logicalDevice instanceof NetworkPort))
				throw new Exception("It is not a correct interface to configure");

			NetworkPort params = copyNetworkPort((NetworkPort) logicalDevice, Integer.parseInt(paramsInterface[1]));
			addVlanId(params, vlanId);
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			printInfo("Sending message to the queue");
			chassisCapability.sendMessage(ActionConstants.SETVLAN, params);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return null;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			printEndCommand();
			return null;
		}
		printEndCommand();
		return null;
	}

	private void addVlanId(NetworkPort params, int vlanId) {
		List<ProtocolEndpoint> protocolEndpoints = params.getProtocolEndpoint();
		// ADD VLAN ID

		// EQUALS IS NOT IMPLEMENTED, WE HAVE TO DELETE THE ELEMENT WITH A INDEX
		int index = 0;
		for (ProtocolEndpoint protocolEndpoint : protocolEndpoints) {
			if (protocolEndpoint instanceof VLANEndpoint) {
				ProtocolEndpoint endpointToRemove = params.getProtocolEndpoint().get(index);
				params.removeProtocolEndpoint(endpointToRemove);
				break;
			}
			index++;
		}
		VLANEndpoint vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(vlanId);
		params.cleanProtocolEndpoint();
		params.addProtocolEndpoint(vlanEndpoint);

	}

	private int containsSubInterface(String nameInterface, int portNumber, ComputerSystem routerModel) {
		int pos = 0;
		for (LogicalDevice logicalDevice : routerModel.getLogicalDevices()) {
			if (logicalDevice.getName().equals(nameInterface)) {
				if (logicalDevice instanceof NetworkPort) {
					if (((NetworkPort) logicalDevice).getPortNumber() == portNumber)
						return pos;
				} else
					return pos;
			}
			pos++;
		}
		return -1;

	}

	private int containsInterface(String nameInterface, ComputerSystem routerModel) {
		int pos = 0;
		for (LogicalDevice logicalDevice : routerModel.getLogicalDevices()) {
			if (logicalDevice.getName().equals(nameInterface)) {
				return pos;
			}
			pos++;
		}
		return -1;

	}

	private boolean isLoopback(String name) {
		return name.startsWith("lo");

	}

	public NetworkPort copyNetworkPort(NetworkPort toCopy, int portNumber) {
		// String name = toCopy.getName();
		NetworkPort networkPortCloned = null;
		if (toCopy.getName().startsWith("lt")) {
			LogicalTunnelPort lt = new LogicalTunnelPort();
			LogicalTunnelPort ltOld = (LogicalTunnelPort) toCopy;
			// params
			lt.setName(ltOld.getName());
			lt.setPortNumber(portNumber);
			lt.setPeer_unit(ltOld.getPeer_unit());
			lt.setLinkTechnology(LinkTechnology.OTHER);
			networkPortCloned = lt;
		} else {
			EthernetPort ethOld = (EthernetPort) toCopy;
			EthernetPort eth = new EthernetPort();
			eth.setName(ethOld.getName());
			eth.setPortNumber(portNumber);
			eth.setLinkTechnology(LinkTechnology.OTHER);
			networkPortCloned = eth;
		}

		boolean vlanFound = true;
		VLANEndpoint vlanEndpointToCopy = null;
		try {
			vlanEndpointToCopy = (VLANEndpoint) getVLANEnpoint(networkPortCloned.getProtocolEndpoint());
		} catch (Exception e) {
			vlanFound = false;
		}

		if (vlanFound) {
			VLANEndpoint vlan = new VLANEndpoint();
			vlan.setVlanID(vlanEndpointToCopy.getVlanID());
			// FIXME NOT USED CLONE!!

			networkPortCloned.addProtocolEndpoint(vlan);
		}

		return networkPortCloned;

	}

	public VLANEndpoint getVLANEnpoint(List<ProtocolEndpoint> protocolEndpoints) throws Exception {
		for (ProtocolEndpoint protocolEndpoint : protocolEndpoints) {
			if (protocolEndpoint instanceof VLANEndpoint) {
				return (VLANEndpoint) protocolEndpoint;
			}
		}
		throw new Exception("VLANEnpoint don't found");

	}

}
