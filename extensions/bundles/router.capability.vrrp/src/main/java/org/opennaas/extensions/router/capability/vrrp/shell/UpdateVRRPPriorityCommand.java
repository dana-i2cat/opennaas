package org.opennaas.extensions.router.capability.vrrp.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

/**
 * @author Julio Carlos Barrera
 * @author Adrian Rosello
 */
@Command(scope = "vrrp", name = "updatePriority", description = "Update VRRP priority")
public class UpdateVRRPPriorityCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to update the VRRP priority", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "vrrpGroupId", description = "The VRRP group ID", required = true, multiValued =
			false)
	private int		vrrpGroupId;

	@Argument(index = 2, name = "priority", description = "The priority of the router inside this VRRP group", required = true, multiValued =
			false)
	private int		priority;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Update VRRP priority");
		try {

			IResource router = getResourceFromFriendlyName(resourceId);

			IVRRPCapability vrrpCapability = (IVRRPCapability) router.getCapabilityByInterface(IVRRPCapability.class);

			VRRPGroup vrrpGroup = new VRRPGroup();
			vrrpGroup.setVrrpName(vrrpGroupId);

			VRRPProtocolEndpoint vrrpEndpoint = new VRRPProtocolEndpoint();
			vrrpEndpoint.setPriority(priority);
			vrrpEndpoint.setService(vrrpGroup);

			vrrpCapability.updateVRRPPriority(vrrpEndpoint);

			printEndCommand();
			return null;
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error updating VRRP priority.");
			printError(e);
			printEndCommand();
			return -1;
		}
	}
}
