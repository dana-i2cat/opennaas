import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.AbstractCapabilityFactory;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

public class MockQueueCapabilityFactory extends AbstractCapabilityFactory {
	public MockQueueCapabilityFactory(String factoryId) {
		this.setType(factoryId);
	}

	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		// TODO Auto-generated method stub
		return this.createCapability(resource.getResourceDescriptor().getCapabilityDescriptor("mock"), resource.getResourceDescriptor().getId());
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		// TODO Auto-generated method stub
		return new MockQueueCapability(capabilityDescriptor);
	}

}
