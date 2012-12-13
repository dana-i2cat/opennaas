package org.opennaas.extensions.router.capability.vrrp.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

/**
 * @author Julio Carlos Barrera
 */
@Command(scope = "vrrp", name = "updateVirtualIPAddress", description = "Update VRRP group virtual IP address")
public class UpdateVRRPVirtualIPAddressCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to update the VRRP virtual IP address", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "vrrpGroupId", description = "The VRRP group ID", required = true, multiValued = false)
	private int		vrrpGroupId;

	@Argument(index = 2, name = "virtualIPAddress", description = "The new VRRP virtual IP address", required = true, multiValued = false)
	private String	virtualIPAddress;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Update VRRP virtual IP address");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			IVRRPCapability vrrpCapability = (IVRRPCapability) router.getCapabilityByInterface(IVRRPCapability.class);

			VRRPGroup vrrpGroup = null;
			List<Service> services = ((ComputerSystem) router.getModel()).getHostedService();
			for (Service service : services) {
				if (service instanceof VRRPGroup) {
					VRRPGroup candidate = (VRRPGroup) service;
					if (candidate.getVrrpName() == vrrpGroupId) {
						vrrpGroup = candidate;
						break;
					}
				}
			}

			VRRPProtocolEndpoint vrrpProtocolEndpoint = null;
			if (vrrpGroup != null) {
				vrrpGroup.setVirtualIPAddress(virtualIPAddress);
				List<ProtocolEndpoint> protocolEndpoints = vrrpGroup.getProtocolEndpoint();
				for (ProtocolEndpoint protocolEndpoint : protocolEndpoints) {
					if (((VRRPGroup) ((VRRPProtocolEndpoint) protocolEndpoint).getService()).getVrrpName() == vrrpGroup.getVrrpName()) {
						vrrpProtocolEndpoint = (VRRPProtocolEndpoint) protocolEndpoint;
						break;
					}
				}
				if (vrrpProtocolEndpoint != null) {
					vrrpCapability.updateVRRPVirtualIPAddress(vrrpProtocolEndpoint);
					return null;
				}
			}
			printError("Error updating VRRP virtual IP address. Not VRRPProtocolEndpoint found");
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error updating VRRP virtual IP address.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
