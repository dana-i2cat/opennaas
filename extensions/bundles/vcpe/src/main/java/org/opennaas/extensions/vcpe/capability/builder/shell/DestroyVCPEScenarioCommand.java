package org.opennaas.extensions.vcpe.capability.builder.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability;

@Command(scope = "vcpenet", name = "destroy", description = "Descroy a vCPE network scenario")
public class DestroyVCPEScenarioCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "vCPENetwork resource friendly name.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		IResource resource = getResourceFromFriendlyName(resourceId);

		IVCPENetworkBuilderCapability capability = (IVCPENetworkBuilderCapability) resource.getCapabilityByInterface(IVCPENetworkBuilderCapability.class);
		capability.destroyVCPENetwork();

		return null;
	}

}
