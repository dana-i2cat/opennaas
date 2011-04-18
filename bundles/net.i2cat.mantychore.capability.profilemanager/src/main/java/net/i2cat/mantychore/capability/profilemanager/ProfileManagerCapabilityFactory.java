package net.i2cat.mantychore.capability.profilemanager;

import java.util.List;

import net.i2cat.nexus.resources.IResource;

public class ProfileManagerCapabilityFactory {
	public ProfileManagerCapability createProfileManagerCapability(List<String> actionIds,
			IResource resource) {
		return new ProfileManagerCapability(actionIds, resource);
	}
}
