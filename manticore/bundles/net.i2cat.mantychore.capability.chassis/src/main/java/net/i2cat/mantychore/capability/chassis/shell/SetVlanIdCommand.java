package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.NetworkPort.LinkTechnology;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "chassis", name = "setVlanId", description = "Set vlan id in a given interface.")
public class SetVlanIdCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "The interface where to set the vlan id", required = true, multiValued = false)
	private String	interfaceName;

	@Argument(index = 2, name = "vlanId", description = "Vlan id to set", required = true, multiValued = false)
	private int		vlanId;

	@Override
	protected Object doExecute() throws Exception {

		try {
			checkArguments();

			LogicalPort iface = createParams(interfaceName, vlanId);

			IResource resource = getResourceFromFriendlyName(resourceId);
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);

			Response response = (Response) chassisCapability.sendMessage(ActionConstants.SET_VLANID, iface);
			printResponseStatus(response);

		} catch (Exception e) {
			printError("Error setting vlan id");
			printError(e);
		}
		return null;
	}

	private LogicalPort createParams(String interfaceName, int vlanId) throws Exception {

		// Assuming a logical interface is specified (that's checked in checkArguments)
		String[] interfaceNameAndPortNumber = splitInterfaces(interfaceName);

		NetworkPort iface = new NetworkPort();
		iface.setName(interfaceNameAndPortNumber[0]);
		iface.setPortNumber(Integer.parseInt(interfaceNameAndPortNumber[1]));
		iface.setLinkTechnology(LinkTechnology.OTHER);
		iface.setOtherLinkTechnology("TAGGED_ETHERNET");

		// specify vlanid in iface
		VLANEndpoint taggedEndpoint = new VLANEndpoint();
		taggedEndpoint.setVlanID(vlanId);
		iface.addProtocolEndpoint(taggedEndpoint);

		return iface;
	}

	private void checkArguments() throws Exception {

		if (isPhysicalInterfaceName(interfaceName)) {
			throw new Exception("Cannot set vlanId to a physical interface");
		}

		// FIXME It is necessary to setvlans in loopback if we want configure LRs
		if (isLoopbackInterfaceName(interfaceName)) {
			throw new UnsupportedOperationException("Vlan configuration in loopback interfaces is not supported.");
		}

		if (!isValidVlanId(vlanId)) {
			throw new Exception("Invalid vlanId. It must fit in 12 bits.");
		}
	}

	private boolean isPhysicalInterfaceName(String interfaceName) {
		return !(interfaceName.contains("."));
	}

	private boolean isLoopbackInterfaceName(String interfaceName) {
		return (interfaceName.startsWith("lo"));
	}

	private boolean isValidVlanId(int vlanId) {
		// The IEEE 802.1Q (VLAN) label.
		// The 12-bit value in the 802.1Q header for Tagged Ethernet.
		return (vlanId > 0 && vlanId < 4096);
	}

}
