package net.i2cat.mantychore.capability.chassis;

import java.util.List;

import net.i2cat.nexus.resources.IResource;

public interface IChassisCapabilityFactory {
	public ChassisCapability createChassisCapability(List<String> actionIds,
			IResource resource);
}
