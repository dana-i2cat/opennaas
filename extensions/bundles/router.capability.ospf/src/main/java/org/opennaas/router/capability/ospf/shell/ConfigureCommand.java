package org.opennaas.router.capability.ospf.shell;

import net.i2cat.mantychore.model.OSPFService;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.router.capability.ospf.OSPFCapability;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "configure", description = "Configure an OSPF service.")
public class ConfigureCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--routerId", aliases = { "-rid" }, description = "RouterId used to identify this router as source of OSPF messages", required = false, multiValued = false)
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

			OSPFCapability ospfCapability = (OSPFCapability) getCapability(router.getCapabilities(), OSPFCapability.CAPABILITY_NAME);
			Response response;
			if (delete) {
				response = ospfCapability.clearOSPFconfiguration(ospfService);
			} else {
				response = ospfCapability.configureOSPF(ospfService);
			}
			return printResponseStatus(response);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring OSPF");
			printError(e);
			printEndCommand();
			return -1;
		}

	}

}