package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.ProtocolEndpoint.ProtocolIFType;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;

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

			LogicalPort iface = createParams(interfaceName, label);

			IResource resource = getResourceFromFriendlyName(resourceId);
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);

			Response response = (Response) chassisCapability.sendMessage(ActionConstants.SETENCAPSULATIONLABEL, iface);
			printResponseStatus(response);

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

	private LogicalPort createParams(String interfaceName, String label) throws Exception {

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

		// specify label in iface
		// we use the name of the endpoint to store the encapsulation label
		// and mark protocolType as unknown (it will be discovered by opennaas)
		ProtocolEndpoint protocolEndpoint = new ProtocolEndpoint();
		protocolEndpoint.setName(label);
		protocolEndpoint.setProtocolIFType(ProtocolIFType.UNKNOWN);
		iface.addProtocolEndpoint(protocolEndpoint);

		return iface;
	}

	private boolean isLoopbackInterfaceName(String interfaceName) {
		return interfaceName.startsWith("lo");
	}

	private boolean isPhysicalInterfaceName(String interfaceName) {
		return !(interfaceName.contains("."));
	}

}
