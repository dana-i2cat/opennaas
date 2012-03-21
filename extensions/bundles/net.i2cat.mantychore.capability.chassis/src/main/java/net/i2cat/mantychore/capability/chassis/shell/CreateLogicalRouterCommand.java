package net.i2cat.mantychore.capability.chassis.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.ComputerSystem;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "chassis", name = "createLogicalRouter", description = "Create a logical router on a given resource.")
public class CreateLogicalRouterCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Host resource name.", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "logicalRouter", description = "Name of the logical router to be created.", required = true, multiValued = false)
	private String			LRname;

	@Argument(index = 2, name = "subinterfaces", description = "Optional list of subinterfaces to transfere to new logical router", required = false, multiValued = true)
	private List<String>	subinterfaces;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("create Logical Router");

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
				printError("Could not get resource with name: " + argsRouterName[0] + ":" + argsRouterName[1]);
				printEndCommand();
				return -1;
			}
			IResource resource = manager.getResource(resourceIdentifier);
			validateResource(resource);
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			// printInfo("Sending message to the queue");

			Response resp = (Response) chassisCapability.sendMessage(ActionConstants.CREATELOGICALROUTER, LRname);

			/* add interfaces */
			// FIXME This kind of logic should go in Capability, not in a karaf command!
			AddInterfaceToLRCommand addInterfaceCommand = new AddInterfaceToLRCommand();
			if (subinterfaces != null) {
				for (String interf : subinterfaces) {
					try {
						ComputerSystem addInterfaceParams = addInterfaceCommand.createLRModelWithInterface("router:" + LRname, interf);
						Response addResp = (Response) chassisCapability.sendMessage(ActionConstants.ADDINTERFACETOLOGICALROUTER, addInterfaceParams);
						if (addResp.getStatus().equals(Response.Status.ERROR)) {
							printError("Can not add " + interf);
							for (String errorMsg : addResp.getErrors()) {
								printError(errorMsg);
							}
						}
					} catch (Exception e) {
						printError("Can not add " + interf);
						printError(e);
					}
				}
			}
			printResponseStatus(resp);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating Logical router.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

}
