package org.opennaas.extensions.network.capability.ospf.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.network.capability.ospf.INetOSPFCapability;

@Command(scope = "netospf", name = "activate", description = "It will activate OSPF in all routers of the network resource.")
public class ActivateCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where OSPF will be activated.", required = true, multiValued = false)
	private String	networkId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Activate OSPF ");
		try {
			IResource network = getResourceFromFriendlyName(networkId);
			INetOSPFCapability netOSPFCapability = (INetOSPFCapability) network.getCapabilityByInterface(INetOSPFCapability.class);
			netOSPFCapability.activateOSPF();
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error activating OSPF");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
