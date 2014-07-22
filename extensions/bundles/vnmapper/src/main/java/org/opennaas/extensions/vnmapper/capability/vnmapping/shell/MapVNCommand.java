package org.opennaas.extensions.vnmapper.capability.vnmapping.shell;

/*
 * #%L
 * OpenNaaS :: VNMapper Resource
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
 * @author Adrian Rosello
 * 
 */
@Command(scope = "vnmapping", name = "mapvn", description = "Map a virtual network request.")
public class MapVNCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The VNMapper resource id.", required = true, multiValued = false)
	private String	vnmapperResourceName;

	@Argument(index = 1, name = "resourceType:resourceName", description = "The network resource id.", required = true, multiValued = false)
	private String	networkResourceName;

	@Argument(index = 2, name = "requestURL", description = "Path to the file containing the request.", required = true, multiValued = false)
	private String	requestFileURL;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Map Virtual Network Request");
		try {

			VNTRequest vnt = new VNTRequest();
			vnt = vnt.readVNTRequestFromXMLFile(requestFileURL);

			IResource vnmapperResource = getResourceFromFriendlyName(vnmapperResourceName);
			VNMappingCapability capab = (VNMappingCapability) vnmapperResource.getCapabilityByType(VNMappingCapability.CAPABILITY_TYPE);

			IResource networkResource = getResourceFromFriendlyName(networkResourceName);
			String resourceId = networkResource.getResourceIdentifier().getId();

			VNMapperOutput capabOutput = capab.mapVN(resourceId, vnt);

			System.out.println(capabOutput.toString());

		} catch (Exception e) {
			printError("Error mapping with resource " + vnmapperResourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
