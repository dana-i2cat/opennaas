package net.i2cat.mantychore.capability.chassis;

import java.util.List;

import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;


public class ChassisCapabilityFactory implements IChassisCapabilityFactory {

	

	public ChassisCapability createChassisCapability(List<String> actionIds, ProtocolSessionContext protocolSessionContext, String resourceId) {
		// TODO Auto-generated method stub
		return new ChassisCapability(actionIds,protocolSessionContext, resourceId);
	}


}
