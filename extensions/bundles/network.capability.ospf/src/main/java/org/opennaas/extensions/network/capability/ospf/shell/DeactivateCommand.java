package org.opennaas.extensions.network.capability.ospf.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.network.capability.ospf.INetOSPFCapability;

@Command(scope = "netospf", name = "deactivate", description = "It will dactivate OSPF in all routers of the network resource.")
public class DeactivateCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where OSPF will be deactivated.", required = true, multiValued = false)
	private String	networkId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Deactivate OSPF ");
		try {
			IResource network = getResourceFromFriendlyName(networkId);
			INetOSPFCapability netOSPFCapability = (INetOSPFCapability) network.getCapabilityByInterface(INetOSPFCapability.class);
			netOSPFCapability.deactivateOSPF();
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error deactivating OSPF");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
