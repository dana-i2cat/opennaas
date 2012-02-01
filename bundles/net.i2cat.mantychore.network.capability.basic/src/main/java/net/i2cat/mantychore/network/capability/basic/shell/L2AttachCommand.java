package net.i2cat.mantychore.network.capability.basic.shell;

import java.util.Iterator;
import java.util.List;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.topology.CrossConnect;
import net.i2cat.mantychore.network.model.topology.Interface;
import net.i2cat.mantychore.network.model.topology.Link;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "net", name = "l2attach", description = "Add a resource to the network")
public class L2AttachCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where to add a resource", required = true, multiValued = false)
	private String	networkId;
	
	@Argument(index = 1, name = "interfaceName", description = "First interface to attach", required = true, multiValued = false)
	private String interface1Name;
	
	@Argument(index = 2, name = "interfaceName", description = "Second interface to attach", required = true, multiValued = false)
	private String interface2Name;
	
	@Override
	protected Object doExecute() throws Exception {
	
		IResource network;
		try {
			network = getResourceFromFriendlyName(networkId);
			if (network == null) {
				printError("Could not load network. Please check network name is correct.");
			}
		} catch (Exception e) {
			printError("Failed to get required resources: " + e.getLocalizedMessage());
			printEndCommand();
			return null;
		}
		
		//get interfaces
		NetworkModel netModel = (NetworkModel) network.getModel();
		List<Interface> interfaces = NetworkModelHelper.getInterfaces(netModel.getNetworkElements());
		
		Iterator<Interface> it = interfaces.iterator();
		boolean found = false;
		Interface interface1 = null;
		Interface interface2 = null;
		while (it.hasNext() && !found) {
			Interface current = it.next();
			if (current.getName().equals(interface1Name)){
				interface1 = current;
			}
			if (current.getName().equals(interface2Name)){
				interface2 = current;
			}
			found = (interface1 != null) && (interface2 != null);
		}
		if (! found) {
			if (interface1 == null)
				printError("Failed to get required interfaces: " + interface1Name + " is not prensent in network model");
			else
				printError("Failed to get required interfaces: " + interface2Name + " is not prensent in network model");
			printEndCommand();
			return null;
		}
		
		//TODO Attach implementation should be moved to an action or internal procedure.
		if (interface1.getDevice().equals(interface2.getDevice())) {
			CrossConnect xConnect = NetworkModelHelper.crossConnectInterfaces(interface1, interface2);
			netModel.getNetworkElements().add(xConnect);
		} else {
			Link link = NetworkModelHelper.linkInterfaces(interface1, interface2, true);
			netModel.getNetworkElements().add(link);
		}
		
		printEndCommand();
		return null;	
	}

}
