package org.opennaas.extensions.router.capability.chassis.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;

@Command(scope = "chassis", name = "setEncapsulation", description = "Set encapsulation in a given interface.")
public class SetEncapsulationCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "The interface where to set the encapsulation.", required = true, multiValued = false)
	private String	interfaceName;

	@Argument(index = 2, name = "encapsulationType", description = "The encapsulation type to use. Possible values: [tagged-ethernet, none]", required = true, multiValued = false)
	private String	encapsulationType;

	@Override
	protected Object doExecute() throws Exception {
		try {
			checkArguments();

			LogicalPort iface = createParams(interfaceName);

			IResource resource = getResourceFromFriendlyName(resourceId);
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);

			Response response = (Response) chassisCapability.sendMessage(ActionConstants.SETENCAPSULATION, iface);
			printResponseStatus(response, resourceId);
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
		ProtocolIFType type = getEncapsulationType(encapsulationType);
		if (!type.equals(ProtocolIFType.UNKNOWN)) {
			ProtocolEndpoint encapsulationEndpoint = new ProtocolEndpoint();
			encapsulationEndpoint.setProtocolIFType(type);
			iface.addProtocolEndpoint(encapsulationEndpoint);
		}

		return iface;
	}

	private boolean isPhysicalInterfaceName(String interfaceName) {
		return !(interfaceName.contains("."));
	}

	private void checkArguments() throws Exception {
		// FIXME It is necessary to setvlans in loopback if we want configure LRs
		if (isLoopbackInterfaceName(interfaceName)) {
			throw new UnsupportedOperationException("Encapsulation in loopback interfaces is not supported.");
		}

		checkEncapsulationType(encapsulationType);
	}

	private void checkEncapsulationType(String encapsulationType) {

		ProtocolIFType type = getEncapsulationType(encapsulationType);

		if (type.equals(ProtocolIFType.OTHER)) {
			throw new UnsupportedOperationException("Unsupported encapsulation type.");
		}
	}

	private ProtocolIFType getEncapsulationType(String encapsulationType) {
		if (encapsulationType.equals("tagged-ethernet")) {
			return ProtocolIFType.LAYER_2_VLAN_USING_802_1Q;
		} else if (encapsulationType.equals("none")) {
			return ProtocolIFType.UNKNOWN;
		} else {
			return ProtocolIFType.OTHER;
		}
	}

	private boolean isLoopbackInterfaceName(String interfaceName) {
		return (interfaceName.startsWith("lo"));
	}

}
