package net.i2cat.mantychore.capability.profilemanager;

import java.util.List;

import net.i2cat.nexus.resources.IResource;

public interface IProfileManagerCapabilityFactory {
	public ProfileManagerCapability createChassisCapability(List<String> actionIds,
			IResource resource);
}
