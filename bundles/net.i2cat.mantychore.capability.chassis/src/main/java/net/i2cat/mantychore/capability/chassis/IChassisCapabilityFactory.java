package net.i2cat.mantychore.capability.chassis;

import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

public interface IChassisCapabilityFactory {
	public ChassisCapability createChassisCapability(ProtocolSessionContext protocolSessionContext, String resourceId);
}
