package org.opennaas.extensions.vnmapper.capability.vnmapping.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.vnmapper.VNTRequest;
import org.opennaas.extensions.vnmapper.capability.vnmapping.VNMapperOutput;
import org.opennaas.extensions.vnmapper.capability.vnmapping.VNMappingCapability;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
@Command(scope = "vnmapping", name = "mapvn", description = "Map a virtual network request.")
public class MapVNCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "requestURL", description = "Path to the file containing the request.", required = true, multiValued = false)
	private String	requestFileURL;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("sayHello");
		try {

			VNTRequest vnt = new VNTRequest();
			vnt = vnt.readVNTRequestFromXMLFile(requestFileURL);

			IResource resource = getResourceFromFriendlyName(resourceName);
			VNMappingCapability capab = (VNMappingCapability) resource.getCapabilityByType(VNMappingCapability.CAPABILITY_TYPE);
			VNMapperOutput capabOutput = capab.mapVN(vnt);

			System.out.println(capabOutput.toString());

		} catch (Exception e) {
			printError("Error mapping with resource " + resourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
