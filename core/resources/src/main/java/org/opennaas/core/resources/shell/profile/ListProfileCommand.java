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

import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "profile", name = "list", description = "List all available profiles")
public class ListProfileCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("profile list");

		IProfileManager manager = getProfileManager();
		List<ProfileDescriptor> listProfiles = manager.listProfiles();
		if (listProfiles == null || listProfiles.isEmpty()) {
			printInfo("No profiles registered.");
			return null;
		}

		for (ProfileDescriptor profileDesc : listProfiles) {
			printInfo("Profile " + profileDesc.getProfileName() + " suitable for resource type : " + profileDesc
					.getResourceType());
		}
		printEndCommand();

		return null;
	}

}
