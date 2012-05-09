package org.opennaas.extensions.router.capability.ospf.shell;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "disableInterface", description = "Disable OSPF in given interfaces")
public class DisableInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "interfaceName", description = "Name of the interface(s) to disable OSPF on", required = true, multiValued = true)
	private List<String>	interfaceNames;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Disable OSPF on interface(s) ");
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

			IOSPFCapability ospfCapability = (IOSPFCapability) router.getCapabilityByInterface(IOSPFCapability.class);
			ospfCapability.disableOSPFInterfaces(ospfPeps);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error disabling OSPF in interfaces(s)");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
