package net.i2cat.nexus.resources.helpers;

import java.util.Map;

import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.profile.IProfile;
import net.i2cat.nexus.resources.profile.ProfileDescriptor;

public class MockProfileFactory {

	public static IProfile newMockProfilefactory(ProfileDescriptor profileDescriptor, Map<String, IActionSet> actionSets) {
		IProfile profile = new MockProfile(profileDescriptor);

		for (String idCapability : actionSets.keySet()) {
			IActionSet actionSet = actionSets.get(idCapability);
			profile.addActionSetForCapability(actionSet, idCapability);
		}

		return profile;

	}

}
