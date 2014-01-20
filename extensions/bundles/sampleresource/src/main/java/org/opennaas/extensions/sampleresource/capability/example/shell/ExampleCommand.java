package org.opennaas.extensions.sampleresource.capability.example.shell;

/*
 * #%L
 * OpenNaaS :: Sample Resource
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
import org.opennaas.extensions.sampleresource.capability.example.ExampleCapability;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
@Command(scope = "example", name = "sayHello", description = "It will say hello.")
public class ExampleCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "userName", description = "The name of the person we will greet.", required = true, multiValued = false)
	private String	username;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("sayHello");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			ExampleCapability capab = (ExampleCapability) resource.getCapabilityByType("example");
			String greeting = capab.sayHello(username);
			printInfo(resourceName + " says : " + greeting);
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
