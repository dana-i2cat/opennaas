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
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFServiceWrapper;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "configure", description = "Configure an OSPF service.")
public class ConfigureCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--routerId", aliases = { "-rid" }, description = "RouterId used to identify this router as source of OSPF messages", required = false, multiValued = false)
	private String	routerId;

	@Option(name = "--delete", aliases = { "-d" }, description = "Delete OSPF service, instead of creating it.")
	boolean			delete;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Configure OSPF ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			OSPFServiceWrapper ospfService = new OSPFServiceWrapper();
			if (routerId != null)
				ospfService.setRouterId(routerId);

			IOSPFCapability ospfCapability = (IOSPFCapability) router.getCapabilityByInterface(IOSPFCapability.class);

			if (delete)
				ospfCapability.clearOSPFconfiguration();
			else
				ospfCapability.configureOSPF(ospfService);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring OSPF");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

}