package org.opennaas.core.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.profile.IProfile;

/**
 * Main resource class
 * 
 * @author Mathieu Lemay
 * @author Isart Canyameres Giménez (i2cat)
 * 
 */
public class Resource implements IResource {

	/** The logger **/
	Log									logger				= LogFactory.getLog(Resource.class);

	/** The resource identifier **/
	private IResourceIdentifier			resourceIdentifier	= null;

	/** The resource current state **/
	private State						state				= null;

	/** The resource descriptor **/
	private ResourceDescriptor			resourceDescriptor	= null;

	/** The resource capabilities **/
	private List<ICapabilityLifecycle>	capabilities		= null;

	/** The resource specific bootstapper class */
	private IResourceBootstrapper		bootstrapper		= null;

	private IModel						model;

	private IProfile					profile				= null;

	public Resource() {
		capabilities = new ArrayList<ICapabilityLifecycle>();
		setState(State.INSTANTIATED);
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public IResourceIdentifier getResourceIdentifier() {
		return resourceIdentifier;
	}

	@Override
	public void initialize() throws IncorrectLifecycleStateException, ResourceException {
		if (!(getState().equals(State.INSTANTIATED) || getState().equals(State.SHUTDOWN)))
			throw new IncorrectLifecycleStateException("Unrecognized transition method (initialize) in state " + getState(), getState());

		// for (int i = 0; i < capabilities.size(); i++) {
		// capabilities.get(i).initialize();
		// }
		setState(State.INITIALIZED);
	}

	@Override
	public void activate() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException {
		if (!getState().equals(State.INITIALIZED))
			throw new IncorrectLifecycleStateException("Unrecognized transition method (activate) in state " + getState(), getState());

		startCapabilities(capabilities.size() - 1, true);

		try {
			if (bootstrapper != null) {
				bootstrapper.bootstrap(this);
			}
		} catch (ResourceException e) {
			try {
				// roll back
				stopCapabilities(capabilities.size() - 1, false);
			} catch (ResourceException re) {
				throw new CorruptStateException("Failed to rollback activate", re);
			} catch (IncorrectLifecycleStateException ie) {
				throw new CorruptStateException("Failed to rollback activate", ie);
			}
			throw e;
		}
		setState(State.ACTIVE);
	}

	@Override
	public void deactivate() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException {
		if (!getState().equals(State.ACTIVE))
			throw new IncorrectLifecycleStateException("Unrecognized transition method (deactivate) in state " + getState(), getState());

		if (bootstrapper != null) {
			bootstrapper.revertBootstrap(this);
		}

		try {
			stopCapabilities(capabilities.size() - 1, true);
		} catch (ResourceException e) {
			// cannot recover revertBootstrap
			markAsCorrupt("Failed to rollback deactivate.", e);
		} catch (IncorrectLifecycleStateException e) {
			// cannot recover revertBootstrap
			markAsCorrupt("Failed to rollback deactivate", e);
		}

		// setState(State.INACTIVE);
		setState(State.INITIALIZED);
	}

	public void forceDeactivate() throws ResourceException {
		String problemMessages = "";

		try {
			if (bootstrapper != null) {
				bootstrapper.revertBootstrap(this);
			}
		} catch (ResourceException e) {
			problemMessages = e.getMessage() + '\n';
		}

		for (ICapabilityLifecycle capability : capabilities) {
			try {
				capability.deactivate();
			} catch (Exception e) {
				problemMessages = e.getMessage() + '\n' + problemMessages;
			}
		}

		if (!problemMessages.isEmpty())
			throw new ResourceException(problemMessages);

		setState(State.INITIALIZED);
	}

	@Override
	public void shutdown() throws IncorrectLifecycleStateException, ResourceException {
		if (!getState().equals(State.INITIALIZED))
			throw new IncorrectLifecycleStateException("Unrecognized transition method (shutdown) in state " + getState(), getState());

		// for (int i = 0; i < capabilities.size(); i++) {
		// capabilities.get(i).shutdown();
		// }
		setState(State.SHUTDOWN);
	}

	@Override
	public ResourceDescriptor getResourceDescriptor() {
		return resourceDescriptor;
	}

	@Override
	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
		this.resourceDescriptor = resourceDescriptor;
	}

	@Override
	public void setResourceIdentifier(IResourceIdentifier resourceIdentifier) {
		this.resourceIdentifier = resourceIdentifier;
	}

	@Override
	public void addCapability(ICapability capability) {
		if (!(capability instanceof ICapabilityLifecycle))
			throw new IllegalArgumentException("Given capability must be of type " + ICapabilityLifecycle.class.getName());

		capabilities.add((ICapabilityLifecycle) capability);
	}

	@Override
	public ICapability getCapabilityByType(String type) throws ResourceException {
		for (ICapability capability : getCapabilities()) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		throw new ResourceException("Error getting capability " + type);
	}

	@Override
	public ICapability getCapability(Information information) {
		Iterator<? extends ICapability> capabilityIterator = getCapabilities().iterator();
		while (capabilityIterator.hasNext()) {
			ICapability capability = capabilityIterator.next();
			if (capability.getCapabilityInformation().equals(information)) {
				return capability;
			}
		}
		return null;
	}

	@Override
	public ICapability removeCapability(Information information) {
		ICapability capability = getCapability(information);
		capabilities.remove(capability);
		return capability;
	}

	@Override
	public List<? extends ICapability> getCapabilities() {
		return capabilities;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setCapabilities(List<? extends ICapability> capabilities) {
		for (ICapability capability : capabilities) {
			if (!(capability instanceof ICapabilityLifecycle))
				throw new IllegalArgumentException("Given capability must be of type " + ICapabilityLifecycle.class.getName());
		}
		this.capabilities = (List<ICapabilityLifecycle>) capabilities;
	}

	@Override
	public List<ICapability> getCapabilitiesByInterface(Class<? extends ICapability> interfaze) {
		List<ICapability> filteredCapabilities = new ArrayList<ICapability>();
		for (ICapability capability : getCapabilities()) {
			if (interfaze.isInstance(capability)) {
				filteredCapabilities.add(capability);
			}
		}
		return filteredCapabilities;
	}

	@Override
	public ICapability getCapabilityByInterface(Class<? extends ICapability> interfaze) throws ResourceException {
		for (ICapability capability : getCapabilities()) {
			if (interfaze.isInstance(capability)) {
				return capability;
			}
		}
		throw new ResourceException("Cannot find capability with interface " + interfaze);
	}

	@Override
	public void start() throws ResourceException, CorruptStateException {
		logger.info("Resource is in " + this.getState()
				+ " state. Trying to start it");

		try {
			// initialize();
			activate();
		} catch (IncorrectLifecycleStateException e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void stop() throws ResourceException, CorruptStateException {
		logger.info("Resource is in " + this.getState()
				+ " state. Trying to stop it");
		try {
			deactivate();
			// shutdown();
		} catch (IncorrectLifecycleStateException e) {
			throw new ResourceException(e);
		}

	}

	/**
	 * @return the bootstrapper
	 */
	@Override
	public IResourceBootstrapper getBootstrapper() {
		return bootstrapper;
	}

	/**
	 * @param bootstrapper
	 *            the bootstrapper to set
	 */
	@Override
	public void setBootstrapper(IResourceBootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	@Override
	public void setModel(IModel model) {
		this.model = model;
	}

	/**
	 * @return the model
	 */
	@Override
	public IModel getModel() {
		return model;
	}

	@Override
	public IProfile getProfile() {
		return profile;
	}

	@Override
	public void setProfile(IProfile profile) {
		this.profile = profile;
	}

	/**
	 * Tries to initialize and activate all capabilities in capabilities [0,lastCapabilityIndex]. If specified, on fail it performs a rollback
	 * operation to leave capabilities in same state as they were
	 * 
	 * @param lastCapabilityIndex
	 * @param rollback
	 *            flag to activate rollback in case of failure
	 * @throws ResourceException
	 * @throws IncorrectLifecycleStateException
	 * @throws CorruptStateException
	 *             if fails to start and fails to roll back, leaving resource in a corrupt state
	 */
	private void startCapabilities(int lastCapabilityIndex, boolean rollback) throws ResourceException, IncorrectLifecycleStateException,
			CorruptStateException {

		if (lastCapabilityIndex < 0)
			return;

		if (lastCapabilityIndex > capabilities.size())
			lastCapabilityIndex = capabilities.size() - 1;

		for (int i = 0; i <= lastCapabilityIndex; i++) {

			try {

				capabilities.get(i).initialize();
				capabilities.get(i).activate();

			} catch (ResourceException e) {

				if (rollback) {
					try {
						if (capabilities.get(i).getState().equals(ICapabilityLifecycle.State.INITIALIZED)) {
							capabilities.get(i).shutdown();
						}
						if (i > 0)
							// stop capabilities without roll back -> if so it may incur in an infinite loop!
							stopCapabilities(i - 1, false);
					} catch (ResourceException re) {
						markAsCorrupt("Failed to roll back startCapabilities.", re);
					} catch (IncorrectLifecycleStateException le) {
						markAsCorrupt("Failed to roll back startCapabilities.", le);
					}
				}
				throw e;

			} catch (IncorrectLifecycleStateException e) {

				if (rollback) {
					try {
						if (capabilities.get(i).getState().equals(ICapabilityLifecycle.State.INITIALIZED)) {
							capabilities.get(i).shutdown();
						}
						if (i > 0)
							// stop capabilities without roll back -> if so it may incur in an infinite loop!
							stopCapabilities(i - 1, false);
					} catch (ResourceException re) {
						markAsCorrupt("Failed to roll back startCapabilities.", re);
					} catch (IncorrectLifecycleStateException le) {
						markAsCorrupt("Failed to roll back startCapabilities.", le);
					}
				}
				throw new ResourceException(e);
			}

		}
	}

	/**
	 * Tries to deactivate and shutdown all capabilities in capabilities [0,lastCapabilityIndex]. If specified, on fail it performs a rollback
	 * operation to leave capabilities in same state as they were
	 * 
	 * @param lastCapabilityIndex
	 * @param rollback
	 *            flag to activate rollback in case of failure
	 * @throws ResourceException
	 * @throws IncorrectLifecycleStateException
	 * @throws CorruptStateException
	 *             if fails to start and fails to roll back, leaving resource in a corrupt state
	 */
	private void stopCapabilities(int lastCapabilityIndex, boolean rollback) throws ResourceException, IncorrectLifecycleStateException,
			CorruptStateException {

		if (lastCapabilityIndex < 0)
			return;

		if (lastCapabilityIndex > capabilities.size())
			lastCapabilityIndex = capabilities.size() - 1;

		for (int i = 0; i <= lastCapabilityIndex; i++) {

			try {

				capabilities.get(i).deactivate();
				capabilities.get(i).shutdown();

			} catch (ResourceException e) {

				if (rollback) {
					try {
						if (capabilities.get(i).getState().equals(ICapabilityLifecycle.State.INITIALIZED)) {
							capabilities.get(i).activate();
						}
						if (i > 0)
							// stop capabilities without roll back -> if so it may incur in an infinite loop!
							startCapabilities(i - 1, false);
					} catch (ResourceException re) {
						markAsCorrupt("Failed to roll back stopCapabilities.", re);
					} catch (IncorrectLifecycleStateException le) {
						markAsCorrupt("Failed to roll back stopCapabilities.", le);
					}
				}
				throw e;

			} catch (IncorrectLifecycleStateException e) {

				if (rollback) {
					try {
						if (capabilities.get(i).getState().equals(ICapabilityLifecycle.State.INITIALIZED)) {
							capabilities.get(i).activate();
						}
						if (i > 0)
							// stop capabilities without roll back -> if so it may incur in an infinite loop!
							startCapabilities(i - 1, false);
					} catch (ResourceException re) {
						markAsCorrupt("Failed to roll back stopCapabilities.", re);
					} catch (IncorrectLifecycleStateException le) {
						markAsCorrupt("Failed to roll back stopCapabilities.", le);
					}
				}
				throw new ResourceException(e);
			}

		}

	}

	/**
	 * @param errorMessage
	 * @param cause
	 * @throws CorruptStateException
	 */
	private void markAsCorrupt(String errorMessage, Throwable cause) throws CorruptStateException {
		setState(State.ERROR);
		throw new CorruptStateException(errorMessage + " Resource is in a corrupt state.", cause);
	}

}
