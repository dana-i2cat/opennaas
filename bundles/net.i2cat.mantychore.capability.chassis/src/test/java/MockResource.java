import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

public class MockResource implements IResource {
	ResourceDescriptor			resourceDescriptor		= new ResourceDescriptor();
	List<CapabilityDescriptor>	capabilityDescriptors	= new ArrayList<CapabilityDescriptor>();

	public void activate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public void deactivate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public void setState(State arg0) {
		// TODO Auto-generated method stub

	}

	public void shutdown() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public void addCapability(ICapability arg0) {
		// TODO Auto-generated method stub

	}

	public List<ICapability> getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	public ICapability getCapability(Information arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ManagedElement getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceDescriptor getResourceDescriptor() {
		// TODO Auto-generated method stub
		return resourceDescriptor;
	}

	public IResourceIdentifier getResourceIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	public ICapability removeCapability(Information arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCapabilities(List<ICapability> arg0) {
		// TODO Auto-generated method stub

	}

	public void setModel(ManagedElement arg0) {
		// TODO Auto-generated method stub

	}

	public void setResourceDescriptor(ResourceDescriptor arg0) {
		// TODO Auto-generated method stub

	}

	public void setResourceIdentifier(IResourceIdentifier arg0) {
		// TODO Auto-generated method stub

	}

	public void start() throws ResourceException {
		// TODO Auto-generated method stub

	}

	public void stop() throws ResourceException {
		// TODO Auto-generated method stub

	}

}
