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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "chassis", name = "removeInterface", description = "Remove a subinterface from a logical router and transfere it to its parent")
public class RemoveInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:ParentResourceName", description = "Parent resource id to add the interface.", required = true, multiValued = false)
	private String	physicalResourceId;

	@Argument(index = 1, name = "resourceType:ChildResourceName", description = "Child resource id owning the interface", required = true, multiValued = false)
	private String	logicalResourceId;

	@Argument(index = 2, name = "interface", description = "The name of the interface to be transfered.", required = true, multiValued = false)
	private String	interfaceName;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Remove Interface from LR ");

		try {
			IResourceManager manager = getResourceManager();

			String[] parentResourceName = new String[2];
			String[] childResourceName = new String[2];

			parentResourceName = super.splitResourceName(physicalResourceId);
			childResourceName = super.splitResourceName(logicalResourceId);

			String[] paramsInterface = super.splitInterfaces(interfaceName);

			IResourceIdentifier parentResourceIdentifier = getResourceIdentifier(manager, parentResourceName);
			IResourceIdentifier childResourceIdentifier = getResourceIdentifier(manager, childResourceName);

			IResource parentResource = manager.getResource(parentResourceIdentifier);
			IResource childResource = manager.getResource(childResourceIdentifier);

			validateResource(parentResource);

			if (isActivatedResource(childResource))
				throw new Exception("The resource is activated");

			/* the physical router have to send the command */
			if (!containsCapability(parentResource.getCapabilities(), ChassisCapability.CHASSIS))
				throw new Exception("Error getting the capability");

			ICapability chassisCapability = getCapability(parentResource.getCapabilities(), ChassisCapability.CHASSIS);

			printInfo("Sending message to the queue");

			/*
			 * FIXME The child resource model is not added because it is not activated. The best option is to search the interface in the parent
			 * resource.
			 */
			ComputerSystem routerModel = ((ComputerSystem) parentResource.getModel());

			int pos = containsInterface(paramsInterface[0], Integer.parseInt(paramsInterface[1]), routerModel);
			if (pos == -1)
				throw new Exception("Given interface is not in parent resource model. Try refreshing the model.");

			LogicalDevice logicalDevice = routerModel.getLogicalDevices().get(pos);
			if (!(logicalDevice instanceof NetworkPort))
				throw new Exception("Given interface is not supported.");

			logicalDevice.setElementName(childResourceName[1]);
			chassisCapability.sendMessage(ActionConstants.DELETESUBINTERFACE, logicalDevice);

			LogicalDevice deviceCopied = copyNetworkPort((NetworkPort) logicalDevice);
			deviceCopied.setElementName(null); // reset logical device

			chassisCapability.sendMessage(ActionConstants.CONFIGURESUBINTERFACE, deviceCopied);

		} catch (Exception e) {
			printError(e);
			printEndCommand();
			return -1;
		}

		printEndCommand();
		return null;
	}

	private boolean isActivatedResource(IResource resource) {
		return resource.getState().equals(ILifecycle.State.ACTIVE);
	}

	private IResourceIdentifier getResourceIdentifier(IResourceManager manager, String[] resourceId) throws Exception {

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(resourceId[0], resourceId[1]);
		if (resourceIdentifier == null)
			throw new Exception("Could not get resource with name: " + resourceId[0] + ":" + resourceId[1]);
		return resourceIdentifier;
	}

	private int containsInterface(String nameInterface, int portNumber, ComputerSystem routerModel) {
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

	public NetworkPort copyNetworkPort(NetworkPort toCopy) {
		// String name = toCopy.getName();
		NetworkPort networkPortCloned = null;
		if (toCopy.getName().startsWith("lt")) {
			LogicalTunnelPort lt = new LogicalTunnelPort();
			LogicalTunnelPort ltOld = (LogicalTunnelPort) toCopy;
			// params
			lt.setName(ltOld.getName());
			lt.setPortNumber(ltOld.getPortNumber());
			lt.setPeer_unit(ltOld.getPeer_unit());
			lt.setLinkTechnology(LinkTechnology.OTHER);
			networkPortCloned = lt;
		} else {
			EthernetPort ethOld = (EthernetPort) toCopy;
			EthernetPort eth = new EthernetPort();
			eth.setName(ethOld.getName());
			eth.setPortNumber(ethOld.getPortNumber());
			eth.setLinkTechnology(LinkTechnology.OTHER);
			networkPortCloned = eth;
		}

		boolean vlanFound = true;
		VLANEndpoint vlanEndpointToCopy = null;
		try {
			vlanEndpointToCopy = (VLANEndpoint) getVLANEnpoint(toCopy.getProtocolEndpoint());
		} catch (Exception e) {
			vlanFound = false;
		}

		if (vlanFound) {
			VLANEndpoint vlan = new VLANEndpoint();
			vlan.setVlanID(vlanEndpointToCopy.getVlanID());
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
		throw new Exception("VLANEnpoint not found");

	}

}
