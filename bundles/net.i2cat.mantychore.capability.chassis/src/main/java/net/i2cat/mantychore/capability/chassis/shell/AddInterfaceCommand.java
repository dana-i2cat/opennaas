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
import net.i2cat.nexus.resources.ILifecycle;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chassis", name = "addInterface", description = "Add an new subinterface in a logical router")
public class AddInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:ParentResourceName", description = "Parent resource id from it is get the interface.", required = true, multiValued = false)
	private String	physicalResourceId;

	@Argument(index = 1, name = "resourceType:ChildResourceName", description = "Child resource id to add the interface.", required = true, multiValued = false)
	private String	logicalResourceId;

	@Argument(index = 2, name = "interface", description = "The name of the interface to be setted.", required = true, multiValued = false)
	private String	interfaceName;

	private boolean	checkTargetResource	= true;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Add an interface to a new resource");

		try {
			IResourceManager manager = getResourceManager();

			String[] sourceResourceName = new String[2];
			String[] targetResourceName = new String[2];

			sourceResourceName = super.splitResourceName(physicalResourceId);
			targetResourceName = super.splitResourceName(logicalResourceId);

			String[] paramsInterface = super.splitInterfaces(interfaceName);

			IResourceIdentifier sourceResourceIdentifier = getResourceIdentifier(manager, sourceResourceName);
			IResource sourceResource = manager.getResource(sourceResourceIdentifier);

			/*
			 * boolean to define if it is necessary to check target resource. It works with the createLogicalRouterCommand because this command don't
			 * need to check if the targetresource-logical resource exists
			 */
			if (checkTargetResource) {
				IResourceIdentifier targetResourceIdentifier = getResourceIdentifier(manager, targetResourceName);
				IResource targetResource = manager.getResource(targetResourceIdentifier);
				if (isActivatedResource(targetResource))
					throw new Exception("The resource is activated");

			}

			super.validateResource(sourceResource);

			/* the physical router have to send the command */
			if (!containsCapability(sourceResource.getCapabilities(), ChassisCapability.CHASSIS))
				throw new Exception("Error getting the capability");

			ICapability chassisCapability = getCapability(sourceResource.getCapabilities(), ChassisCapability.CHASSIS);

			printInfo("Sending message to the queue");

			ComputerSystem routerModel = ((ComputerSystem) sourceResource.getModel());
			int pos = containsSubInterface(paramsInterface[0], Integer.parseInt(paramsInterface[1]), routerModel); // change to containssub
			if (pos == -1)
				throw new Exception("The Physical router don't have the interface");

			LogicalDevice logicalDevice = routerModel.getLogicalDevices().get(pos);
			if (!(logicalDevice instanceof NetworkPort))
				throw new Exception("It is not a correct interface to configure");

			chassisCapability.sendMessage(ActionConstants.DELETESUBINTERFACE, logicalDevice);

			LogicalDevice deviceCopied = copyNetworkPort((NetworkPort) logicalDevice);
			deviceCopied.setElementName(targetResourceName[1]);

			chassisCapability.sendMessage(ActionConstants.SETVLAN, deviceCopied);

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
			throw new Exception("Error in identifier");
		return resourceIdentifier;
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
		throw new Exception("VLANEnpoint don't found");

	}

	/* helper methods to call manually the commands */
	public String getPhysicalResourceId() {
		return physicalResourceId;
	}

	public void setPhysicalResourceId(String physicalResourceId) {
		this.physicalResourceId = physicalResourceId;
	}

	public String getLogicalResourceId() {
		return logicalResourceId;
	}

	public void setLogicalResourceId(String logicalResourceId) {
		this.logicalResourceId = logicalResourceId;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @param checkTargetResource
	 *            the checkTargetResource to set
	 */
	public void setCheckTargetResource(boolean checkTargetResource) {
		this.checkTargetResource = checkTargetResource;
	}

	/**
	 * @return the checkTargetResource
	 */
	public boolean isCheckTargetResource() {
		return checkTargetResource;
	}

}
