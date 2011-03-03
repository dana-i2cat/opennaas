package net.i2cat.mantychore.capability.chassis;

//import net.i2cat.nexus.resources.capability.CapabilityException;
//import net.i2cat.nexus.resources.capability.ICapability;
//import net.i2cat.nexus.resources.capability.ICapabilityFactory;
//import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

public class ChassisCapabilityFactory implements IChassisCapabilityFactory {

//	public ICapability create(CapabilityDescriptor moduleDescriptor, String resourceId) throws CapabilityException {
//		// TODO Auto-generated method stub
//		ChassisCapability chassiscapability = new ChassisCapability(resourceId);
//		chassiscapability.setCapabilityDescriptor(moduleDescriptor);
//		return chassiscapability;
//	}

	public ChassisCapability createChassisCapability(String resourceId) {
		// TODO Auto-generated method stub
		return new ChassisCapability(resourceId);
	}

}
