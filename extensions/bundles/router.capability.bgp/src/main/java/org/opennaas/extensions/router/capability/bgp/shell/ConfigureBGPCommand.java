package org.opennaas.extensions.router.capability.bgp.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.bgp.BGPModelFactory;
import org.opennaas.extensions.router.capability.bgp.IBGPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;

@Command(scope = "bgp", name = "configureBGP", description = "Configure BGP in a router")
public class ConfigureBGPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:ParentResourceName", description = "Parent resource id, source of the transference", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "filePath", description = "Absolut path to properties file to load BGP from", required = true, multiValued = false)
	private String	filePath;

	@Override
	protected Object doExecute() throws Exception {

		try {

			BGPModelFactory factory = new BGPModelFactory(filePath);
			ComputerSystem model = factory.createRouterWithBGP();

			IResource sourceResource = getResourceFromFriendlyName(resourceId);

			IBGPCapability chassisCapability = (IBGPCapability) sourceResource.getCapabilityByInterface(IBGPCapability.class);
			chassisCapability.configureBGP(model);

		} catch (Exception e) {
			printError(e);
			printEndCommand();
			return -1;
		}

		return null;
	}

}
