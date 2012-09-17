package org.opennaas.extensions.router.capability.chassis.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.NetworkPort;

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

			// create call params
			ComputerSystem logicalRouterModelWithInterfaces = createLRModelWithInterfaces("router:" + LRname, subinterfaces);

			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);
			chassisCapability.createLogicalRouter(logicalRouterModelWithInterfaces);

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

	private ComputerSystem createLRModelWithInterfaces(String logicalRouterName, List<String> subinterfaces) throws Exception {
		ComputerSystem lrModel = createLRModel(logicalRouterName);

		if (subinterfaces != null) {
			for (String interfaceName : subinterfaces) {
				NetworkPort iface = createInterface(interfaceName);
				lrModel.addLogicalDevice(iface);
			}
		}
		return lrModel;
	}

	public ComputerSystem createLRModel(String logicalRouterFriendlyId) throws Exception {
		// That's a hack for not requiring logicalRouter to be already added in the resource manager when this command is executed.
		// Instead of getting the resource using resource manager, we take logicalRouter name from the friendly id.
		ComputerSystem logicalRouterModel = new ComputerSystem();
		String[] targetResourceName = splitResourceName(logicalRouterFriendlyId);
		logicalRouterModel.setName(targetResourceName[1]);
		logicalRouterModel.setElementName(targetResourceName[1]);

		return logicalRouterModel;
	}

	public NetworkPort createInterface(String interfaceNameWithPort) throws Exception {
		// That's a hack for not requiring interface to be already created in opennaas model when this command is executed.
		// Instead of getting it from physical router model, we use only the interface identifier.
		// Action will fail (in execute) if this interface is not created
		String[] paramsInterface = splitInterfaces(interfaceNameWithPort);
		NetworkPort iface = new NetworkPort();
		iface.setName(paramsInterface[0]);
		iface.setPortNumber(Integer.parseInt(paramsInterface[1]));

		return iface;
	}

}
