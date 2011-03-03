package net.i2cat.mantychore.capability.chassis;

import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;


public class ChassisCapabilityFactory implements IChassisCapabilityFactory {

	

	public ChassisCapability createChassisCapability(ProtocolSessionContext protocolSessionContext, String resourceId) {
		// TODO Auto-generated method stub
		return new ChassisCapability(protocolSessionContext, resourceId);
	}

}
