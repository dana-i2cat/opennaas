package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chassis", name = "createSubInterface", description = "Create a subinterface on a given resource.")
public class CreateSubIntefaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "subInterface", description = "The interface to be created.", required = true, multiValued = false)
	private String	subinterface;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("create  subInterfaces");

		try {
			IResourceManager manager = getResourceManager();

			if (!splitResourceName(resourceId))
				return -1;

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			printInfo("Sending message to the queue");
			chassisCapability.sendMessage(ActionConstants.CREATESUBINTERFACE, prepareParams());

		} catch (ResourceException e) {
			printError(e);
			endcommand();
			return -1;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			endcommand();
			return -1;
		}
		endcommand();
		return null;
	}

	private boolean validateResource(IResource resource) throws ResourceException {
		if (resource == null)
			throw new ResourceException("No resource found.");
		if (resource.getModel() == null)
			throw new ResourceException("The resource didn't have a model initialized. Start the resource first.");
		if (resource.getCapabilities() == null) {
			throw new ResourceException("The resource didn't have the capabilities initialized. Start the resource first.");
		}
		return true;
	}

	private EthernetPort prepareParams() {
		String[] args = subinterface.split(".");
		EthernetPort eth = new EthernetPort();
		eth.setName(args[0]);
		eth.setPortNumber(Integer.parseInt(args[1]));
		return eth;
	}
}
