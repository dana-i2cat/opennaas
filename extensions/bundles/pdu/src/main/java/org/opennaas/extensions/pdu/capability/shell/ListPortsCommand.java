package org.opennaas.extensions.pdu.capability.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.pdu.model.PDUModel;

@Command(scope = "pdu", name = "listPorts", description = "Return ports known to this pdu")
public class ListPortsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id where given port is.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("listPorts of pdu: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		List<PowerSource> ports = ((PDUModel) resource.getModel()).getPdu().getPowerSources();

		printPorts(ports);

		printEndCommand();
		return null;
	}

	private void printPorts(List<PowerSource> ports) {
		for (PowerSource port : ports) {
			printSymbol("Port id:" + port.getId());
		}
	}

}
