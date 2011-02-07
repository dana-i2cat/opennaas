package net.i2cat.mantychore.core.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.mantychore.core.resources.capability.ICapability;
import net.i2cat.mantychore.core.resources.descriptor.ResourceDescriptor;
import net.i2cat.mantychore.core.resources.descriptor.Information;

/**
 * Main resource class
 * 
 * @author Mathieu Lemay
 * 
 */
public class Resource implements IResource {

	/** The logger **/
	Logger logger = LoggerFactory.getLogger(Resource.class);

	/** The resource identifier **/
	private IResourceIdentifier resourceIdentifier = null;

	/** The resource current state **/
	private State state = null;

	/** The resource descriptor **/
	private ResourceDescriptor resourceDescriptor = null;

	/** The resource capabilities **/
	private List<ICapability> capabilities = null;

	/** The resource specific bootstapper class */
	private IResourceBootstrapper bootstrapper = null;

	public Resource() {
		capabilities = new ArrayList<ICapability>();
		setState(State.INSTANTIATED);
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public IResourceIdentifier getResourceIdentifier() {
		return resourceIdentifier;
	}

	public void initialize() throws ResourceException {
		if (getState().equals(State.INSTANTIATED) || getState().equals(State.SHUTDOWN)){
			for (int i = 0; i < capabilities.size(); i++) {
				capabilities.get(i).initialize();
			}
			setState(State.INITIALIZED);
		}
	}

	public void activate() throws ResourceException {
		if (getState().equals(State.INITIALIZED) || getState().equals(State.INACTIVE)){
			for (int i = 0; i < capabilities.size(); i++) {
				capabilities.get(i).activate();
			}
			if (bootstrapper != null) {
				bootstrapper.bootstrap(this);
			}
			setState(State.ACTIVE);
		}
	}

	public void deactivate() throws ResourceException {
		if (getState().equals(State.ACTIVE)){
			for (int i = 0; i < capabilities.size(); i++) {
				capabilities.get(i).deactivate();
			}
			setState(State.INACTIVE);
		}
	}

	public void shutdown() throws ResourceException {
		if (getState().equals(State.INACTIVE)){
			for (int i = 0; i < capabilities.size(); i++) {
				capabilities.get(i).shutdown();
			}
			setState(State.SHUTDOWN);
		}
	}

	public ResourceDescriptor getResourceDescriptor() {
		return resourceDescriptor;
	}

	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
		this.resourceDescriptor = resourceDescriptor;
	}

	public void setResourceIdentifier(IResourceIdentifier resourceIdentifier) {
		this.resourceIdentifier = resourceIdentifier;
	}

	public void addCapability(ICapability capability) {
		capabilities.add(capability);
	}

	public ICapability getCapability(Information information) {
		Iterator<ICapability> capabilityIterator = capabilities.iterator();
		while (capabilityIterator.hasNext()) {
			ICapability capability = capabilityIterator.next();
			if (capability.getCapabilityInformation().equals(information)) {
				return capability;
			}
		}
		return null;
	}

	public ICapability removeCapability(Information information) {
		ICapability capability = getCapability(information);
		capabilities.remove(capability);
		return capability;
	}

	public List<ICapability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(List<ICapability> capabilities) {
		this.capabilities = capabilities;
	}

	public void start() throws ResourceException {
		logger.info("Resource is in "+ this.getState()+" state. Trying to start it");
		if (getState().equals(State.INSTANTIATED) || getState().equals(State.SHUTDOWN)){
			initialize();
			activate();
		}
	}

	public void stop() throws ResourceException {
		logger.info("Resource is in "+ this.getState()+" state. Trying to stop it");
		if (getState().equals(State.ACTIVE)){
			deactivate();
			shutdown();
		}
	}

	/**
	 * @return the bootstrapper
	 */
	public IResourceBootstrapper getBootstrapper() {
		return bootstrapper;
	}

	/**
	 * @param bootstrapper
	 *            the bootstrapper to set
	 */
	public void setBootstrapper(IResourceBootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}
}