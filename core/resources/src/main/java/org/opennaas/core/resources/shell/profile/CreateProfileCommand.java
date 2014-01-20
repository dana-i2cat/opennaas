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

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Add a Profile to ProfileManager. <br/>
 * NOTE: Profiles are automatically added when an OSGi bundle exporting IProfile interface is registered in OSGi registry.
 * 
 * @author Evelyn Torras (i2cat)
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@Command(scope = "profile", name = "create", description = "Create a new Profile.")
public class CreateProfileCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "profile identifier ", description = "The name or identifier of the profile.", required = true, multiValued = false)
	private String			profileID;
	// FIXME is a type or an ID???? List or only one variable
	@Argument(index = 1, name = "resourceType", description = "The resource type associated to this profile.", required = true, multiValued = true)
	private List<String>	resourceType;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("profile info");

		IProfileManager manager = getProfileManager();
		IProfile profile = null;
		// FIXME how to obtain Profile??????
		// manager.addProfile(profile);

		printInfo("NOT IMPLEMENTED");
		return null;
	}

}
