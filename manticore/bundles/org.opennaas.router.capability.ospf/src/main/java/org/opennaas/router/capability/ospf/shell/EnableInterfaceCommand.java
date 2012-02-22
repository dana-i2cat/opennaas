package org.opennaas.router.capability.ospf.shell;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.OSPFProtocolEndpoint;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.router.capability.ospf.OSPFCapability;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "enableInterface", description = "Enable OSPF in given interfaces")
public class EnableInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "interfaceName", description = "Name of the interface(s) to enable OSPF on", required = true, multiValued = true)
	private List<String>	interfaceNames;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Enable OSPF on interface(s) ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			// FIXME Cannot read model to get OSPFProtocolEndpoints and their OSPFArea.
			// model may not be updated :S

			List<OSPFProtocolEndpoint> ospfPeps = new ArrayList<OSPFProtocolEndpoint>(interfaceNames.size());
			OSPFProtocolEndpoint pep;
			for (String ifaceName : interfaceNames) {
				pep = new OSPFProtocolEndpoint();
				pep.setName(ifaceName);
				ospfPeps.add(pep);
			}

			OSPFCapability ospfCapability = (OSPFCapability) getCapability(router.getCapabilities(), OSPFCapability.CAPABILITY_NAME);
			Response response = ospfCapability.enableOSPFInterfaces(ospfPeps);
			return printResponseStatus(response);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error enabling OSPF in interface(s)");
			printError(e);
			printEndCommand();
			return -1;
		}
	}
}