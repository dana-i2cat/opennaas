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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Show the Profile information of one or more resources
 * 
 * @author Evelyn Torras
 * 
 */
@Command(scope = "profile", name = "info", description = "Provides extended information about one or more profiles.")
public class InfoProfileCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "profile ids", description = "A space delimited list of profiles ids.", required = true, multiValued = true)
	private List<String>	profilesIDs;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("profile information");
		boolean find;
		IProfileManager manager = getProfileManager();
		List<ProfileDescriptor> profiles = manager.listProfiles();

		for (String profileName : profilesIDs) {
			find = false;
			for (ProfileDescriptor profileDesc : profiles) {
				if (profileDesc.getProfileName().equalsIgnoreCase(profileName)) {

					printInfo("Profile " + profileDesc.getProfileName());
					printInfo("Resource type: " + profileDesc.getResourceType());

					printSymbol(horizontalSeparator);
					IProfile profile = manager.getProfile(profileName);
					printInfo("Actions");
					for (String capabilityId : profile.getActionSets().keySet()) {
						printInfo(doubleTab + "Capability " + capabilityId + " with ActionSetId " + profile.getActionSetForCapability(
								capabilityId)
								.getActionSetId());
						for (String actionName : profile.getActionSetForCapability(capabilityId).getActionNames()) {
							printInfo(doubleTab + indexArrowRigth + "Action: " + actionName);
						}
					}
					printSymbol(horizontalSeparator);
					printInfo("Associated resources");
					List<IResource> resources = manager.getRegisteredResources(profileDesc.getProfileName());
					if (resources.isEmpty()) {
						printInfo("There are no associated resources");
					}
					for (IResource resource : resources) {
						printInfo("Resource: " + resource.getResourceDescriptor().getInformation().getType() + ":" + resource
								.getResourceDescriptor().getInformation().getName());

						// for (String capabilityId : resource.getProfile().getActionSets().keySet()) {
						// out.println("	Capability: " + capabilityId + " ActionSet: " + resource.getProfile()
						// .getActionSetForCapability(capabilityId).getActionSetId());
						// for (String action : resource.getProfile().getActionSetForCapability(capabilityId).getActionNames()) {
						// out.println(">>>>>>> Action: " + action);
						// }
						// }
					}
					printSymbol(underLine);
					find = true;
				}
			}
			if (!find) {
				printError("Wrong profile id: " + profileName);
			}
		}
		printEndCommand();
		return null;
	}
}
