package org.opennaas.extensions.router.capability.chassis.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;

@Command(scope = "chassis", name = "setEncapsulationlabel", description = "Set an encapsulation label in a given interface.")
public class SetEncapsulationLabelCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "The interface where to set the encapsulation label", required = true, multiValued = false)
	private String	interfaceName;

	@Argument(index = 2, name = "label", description = "the label value", required = false, multiValued = false)
	private String	label	= "";

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("set Encapsulation label");

		try {
			checkArguments();

			LogicalPort iface = createParams(interfaceName);

			IResource resource = getResourceFromFriendlyName(resourceId);

			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);
			chassisCapability.setEncapsulationLabel(iface, label);

		} catch (Exception e) {
			printError("Error setting vlan.");
			printError(e);
			printEndCommand();
			return null;
		}

		printEndCommand();
		return null;
	}

	private void checkArguments() throws Exception {
		// FIXME It is necessary to setvlans in loopback if we want configure LRs
		if (isLoopbackInterfaceName(interfaceName)) {
			throw new UnsupportedOperationException("Encapsulation in loopback interfaces is not supported.");
		}
	}

	private LogicalPort createParams(String interfaceName) throws Exception {

		LogicalPort iface;
		if (isPhysicalInterfaceName(interfaceName)) {
			iface = new LogicalPort();
			iface.setName(interfaceName);
		} else {
			iface = new NetworkPort();
			String[] interfaceNameAndPortNumber = splitInterfaces(interfaceName);
			iface.setName(interfaceNameAndPortNumber[0]);
			((NetworkPort) iface).setPortNumber(Integer.parseInt(interfaceNameAndPortNumber[1]));
		}
		return iface;
	}

	private boolean isLoopbackInterfaceName(String interfaceName) {
		return interfaceName.startsWith("lo");
	}

	private boolean isPhysicalInterfaceName(String interfaceName) {
		return !(interfaceName.contains("."));
	}

}
