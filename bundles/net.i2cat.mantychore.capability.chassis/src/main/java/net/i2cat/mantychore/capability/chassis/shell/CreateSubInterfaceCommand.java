package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.NetworkPort.LinkTechnology;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

@Command(scope = "chassis", name = "createSubInterface", description = "Create a subinterface on a given resource.")
public class CreateSubInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "subInterface", description = "The interface to be created.", required = true, multiValued = false)
	private String	subinterface;
	
	@Option(name = "--description", aliases = { "-d" }, description = "description info.")
	private String	description = "";

	@Option(name = "--vlanid", aliases = { "-v" }, description = "specify vlan id to use vlan-tagging.")
	private int	vlanid;

	

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("create  subInterfaces");

		try {
			IResourceManager manager = getResourceManager();

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				printEndCommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			printInfo("Sending message to the queue");
			chassisCapability.sendMessage(ActionConstants.CONFIGURESUBINTERFACE, prepareParams());

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring interfaces.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	private EthernetPort prepareParams() {
		String[] args = subinterface.split("\\.");
		EthernetPort eth = new EthernetPort();
		eth.setName(args[0]);
		eth.setPortNumber(Integer.parseInt(args[1]));
		VLANEndpoint vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(vlanid); //TODO COMPLETE OTHER CASES... INITIALIZE THE VLAN ID TO 1
		eth.addProtocolEndpoint(vlanEndpoint);
		eth.setDescription(description);
		return eth;
	}
}
