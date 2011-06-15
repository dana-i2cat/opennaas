import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.profile.IProfile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockCapability implements ICapability {
	Log	log	= LogFactory.getLog(MockCapability.class);

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(State state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public CapabilityDescriptor getCapabilityDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public Information getCapabilityInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResource(IResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException {
		log.info("MOCK CAPABILITY: send message!!");
		return null;
	}

}
