package org.opennaas.core.resources.shell.profile;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Remove Profile from one or more resources
 * 
 * @author Evelyn Torras (i2cat)
 * @author Isart Canyameres Gimenez (i2cat)
 */
@Command(scope = "profile", name = "remove", description = "Remove a profile from available ones.")
public class RemoveProfileCommand extends GenericKarafCommand {

	@Argument(name = "profileName", index = 0, required = true, description = "The profile Id to be deleted.")
	String	profileName	= null;

	// @Option(name = "--resources", aliases = { "-r" }, description = "The list of resources to delete the profile")
	// boolean optionResources;
	// @Argument(index = 1, name = "resource ids", description = "A space delimited list of resource ids.", required = false, multiValued = true)
	// private List<String> resourceIDs;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("remove profile");

		IProfileManager manager = getProfileManager();
		try {
			manager.removeProfile(profileName);
			printInfo("The profile " + profileName + " has been deleted succesfully");
		} catch (ResourceException e) {
			printError("Error removing profile ");
			printError(e);
			throw e;
		}
		printEndCommand();
		return null;
	}
}
