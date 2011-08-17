package org.opennaas.core.resources.shell.profile;

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

		initcommand("profile information");
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
		endcommand();
		return null;
	}
}
