package org.opennaas.extensions.router.capability.ospfv3.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospfv3.IOSPFv3Capability;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.RouteCalculationService.AlgorithmType;

@Command(scope = "ospfv3", name = "configure", description = "Configure an OSPFv3 service.")
public class ConfigureCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--routerId", aliases = { "-rid" }, description = "RouterId used to identify this router as source of OSPFv3 messages", required = false, multiValued = false)
	private String	routerId;

	@Option(name = "--delete", aliases = { "-d" }, description = "Delete OSPF service, instead of creating it.")
	boolean			delete;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Configure OSPF ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			OSPFService ospfService = new OSPFService();
			if (routerId != null) {
				ospfService.setRouterID(routerId);
			}
			ospfService.setAlgorithmType(AlgorithmType.OSPFV3);

			IOSPFv3Capability ospfCapability = (IOSPFv3Capability) router.getCapabilityByInterface(IOSPFv3Capability.class);

			if (delete) {
				ospfCapability.clearOSPFv3configuration(ospfService);
			} else {
				ospfCapability.configureOSPFv3(ospfService);
			}
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring OSPFv3");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

}