package org.opennaas.extensions.vnmapper.capability.example.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.vnmapper.VNState;
import org.opennaas.extensions.vnmapper.VNTRequest;
import org.opennaas.extensions.vnmapper.capability.example.ExampleCapability;
import org.opennaas.extensions.vnmapper.capability.example.VNMapperOutput;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
@Command(scope = "vnmapping", name = "mapvn", description = "It will say hello.")
public class ExampleCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "userName", description = "The name of the person we will greet.", required = true, multiValued = false)
	private String	username;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("sayHello");
		try {

			VNTRequest vnt = new VNTRequest();
			vnt = vnt.readVNTRequestFromXMLFile(username);

			IResource resource = getResourceFromFriendlyName(resourceName);
			ExampleCapability capab = (ExampleCapability) resource.getCapabilityByType(ExampleCapability.CAPABILITY_TYPE);
			VNMapperOutput capabOutput = capab.sayHello(vnt);

			VNState matchingState = capabOutput.getResult().getMatchingState();
			VNState mappingState = capabOutput.getResult().getMappingState();

			System.out.println(capabOutput.getMapperInput().getNet().toString());
			System.out.println(capabOutput.getMapperInput().getRequest().toString());

			if (!matchingState.equals(VNState.SUCCESSFUL))
				System.out.println("Unsuccessful matching");
			else if (matchingState.equals(VNState.SUCCESSFUL) && !mappingState.equals(VNState.SUCCESSFUL)) {
				System.out.println("Successful Matching");
				System.out.println("Unsucessful Mapping");

			}
			else {
				System.out.println("Successful Matching");
				System.out.println("Sucessful Mapping");
				System.out.println(capabOutput.getResult().toString());
			}
		} catch (Exception e) {
			printError("Error greeting from resource " + resourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
