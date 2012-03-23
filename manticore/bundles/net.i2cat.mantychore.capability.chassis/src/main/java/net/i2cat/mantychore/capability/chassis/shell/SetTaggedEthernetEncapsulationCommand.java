package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.LogicalPort.PortType;
import net.i2cat.mantychore.model.NetworkPort;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "chassis", name = "setTaggedEthernetEncapsulation", description = "Set tagged ethernet encapsulation in a given interface.")
public class SetTaggedEthernetEncapsulationCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "The interface where to set the encapsulation.", required = true, multiValued = false)
	private String	interfaceName;

	@Override
	protected Object doExecute() throws Exception {
		try {
			checkArguments();

			LogicalPort iface = createParams(interfaceName);

			IResource resource = getResourceFromFriendlyName(resourceId);
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);

			Response response = (Response) chassisCapability.sendMessage(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION, iface);
			printResponseStatus(response);
		} catch (Exception e) {
			printError("Error setting encapsulation");
			printError(e);
		}
		return null;
	}

	private LogicalPort createParams(String interfaceName) throws Exception {

		LogicalPort iface;
		if (isPhysicalInterfaceName(interfaceName)) {
			// physical interface specified
			iface = new LogicalPort();
			iface.setName(interfaceName);
		} else {
			// logical interface specified
			String[] interfaceNameAndPortNumber = splitInterfaces(interfaceName);
			iface = new NetworkPort();
			iface.setName(interfaceNameAndPortNumber[0]);
			((NetworkPort) iface).setPortNumber(Integer.parseInt(interfaceNameAndPortNumber[1]));
		}

		// specify encapsulation type in iface
		iface.setPortType(PortType.OTHER);
		iface.setOtherPortType("TAGGED_ETHERNET");

		return iface;
	}

	private boolean isPhysicalInterfaceName(String interfaceName) {
		return !(interfaceName.contains("."));
	}

	private void checkArguments() throws Exception {
		// FIXME It is necessary to setvlans in loopback if we want configure LRs
		if (isLoopbackInterfaceName(interfaceName)) {
			throw new UnsupportedOperationException("Tagged ethernet encapsulation in loopback interfaces is not supported.");
		}
	}

	private boolean isLoopbackInterfaceName(String interfaceName) {
		return (interfaceName.startsWith("lo"));
	}

}
