package org.opennaas.extensions.router.capability.ospf.shell;

/*
 * #%L
 * OpenNaaS :: Router :: OSPF capability
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
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFArea.AreaType;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "configureArea", description = "Configure an OSPF area")
public class ConfigureAreaCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "areaId", description = "OSPF area id.", required = true, multiValued = false)
	private String	areaId;

	@Option(name = "--areaType", aliases = { "-t" }, description = "OSPF area type. Accepted values: [PLAIN, STUB, NSSA]", required = false, multiValued = false)
	private String	areaType	= "PLAIN";

	@Option(name = "--delete", aliases = { "-d" }, description = "Delete given area, instead of creating it.")
	boolean			delete;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Configure OSPF area ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			// FIXME Cannot read model to get interfaces.
			// model may not be updated :S

			AreaType selectedAreaType = null;
			for (AreaType type : AreaType.values()) {
				if (areaType.equals(type.toString()))
					selectedAreaType = type;
			}
			if (selectedAreaType == null) {
				throw new Exception("Invalid area type: " + areaType);
			}

			OSPFArea area = new OSPFArea();
			area.setAreaID(ModelHelper.ipv4StringToLong(areaId));
			area.setAreaType(selectedAreaType);

			OSPFAreaConfiguration areaConfig = new OSPFAreaConfiguration();
			areaConfig.setOSPFArea(area);

			IOSPFCapability ospfCapability = (IOSPFCapability) router.getCapabilityByInterface(IOSPFCapability.class);

			if (delete) {
				ospfCapability.removeOSPFArea(areaConfig);
			} else {
				ospfCapability.configureOSPFArea(areaConfig);
			}
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring OSPF area");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

}