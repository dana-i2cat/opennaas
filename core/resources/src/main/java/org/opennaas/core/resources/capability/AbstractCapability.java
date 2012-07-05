package org.opennaas.core.resources.capability;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.profile.IProfile;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * This class provides an abstract implementation for the ICapabilityLifecycle and IQueueingCapability interface.
 * 
 * This class must be extended by each capability using actions and a queue. It provides a default implementation of ICapabilityLifecycle methods.
 * Those methods may be overridden in children classes if needed.
 * 
 * @author Mathieu Lemay (ITI)
 * @author Scott Campbell(CRC)
 * @author Eduard Grasa (i2CAT)
 * @author Carlos Baez (i2CAT)
 * @author Isart Canyameres (i2CAT) - Make it implement ICapabilityLifecycle and IQueueingCapability
 * 
 */
public abstract class AbstractCapability implements ICapabilityLifecycle, IQueueingCapability {

	private static final String		REFRESH			= "refresh";

	Log								log				= LogFactory.getLog(AbstractCapability.class);

	/** The descriptor for this capability **/
	private State					state			= null;
	protected CapabilityDescriptor	descriptor;
	protected String				capabilityId	= null;
	protected IResource				resource		= null;
	protected IActionSet			actionSet		= null;
	protected IProfile				profile			= null;
	protected ServiceRegistration	registration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.IQueueingCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public abstract void queueAction(IAction action) throws CapabilityException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.IQueueingCapability#getActionSet()
	 */
	@Override
	public abstract IActionSet getActionSet() throws CapabilityException;

	public AbstractCapability(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
		this.capabilityId = descriptor.getCapabilityInformation().getType();
		setState(State.INSTANTIATED);
	}

	/**
	 * @return the resource name through the resource descriptor
	 */
	public String getResourceName() {
		return resource.getResourceDescriptor().getInformation().getName();
	}

	/**
	 * @return the resource name through the resource descriptor
	 */
	public String getResourceType() {
		return resource.getResourceDescriptor().getInformation().getType();
	}

	// IQueueingCapability methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityDescriptor()
	 */
	@Override
	public CapabilityDescriptor getCapabilityDescriptor() {
		return descriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#setCapabilityDescriptor(org.opennaas.core.resources.descriptor.CapabilityDescriptor)
	 */
	@Override
	public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityInformation()
	 */
	@Override
	public Information getCapabilityInformation() {
		return descriptor.getCapabilityInformation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (getCapabilityInformation() != null) {
			builder.append("\nCapability Type: " + getCapabilityInformation().getType());
			builder.append("\nCapability Description: " + getCapabilityInformation().getDescription());
			builder.append("\nCapability Version: " + getCapabilityInformation().getName());
		}
		return builder.toString();
	}

	/**
	 * The resource where this capability belongs
	 * 
	 * @param resource
	 */
	@Override
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	// ICapabilityLifecycle methods

	/**
	 * Returns the current capability state
	 * 
	 * @return state enum object
	 */
	@Override
	public State getState() {
		return state;
	}

	/**
	 * Sets the current capability state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Initializes this capability, the status will be INITIALIZED, then will be ACTIVE if enabled.
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void initialize() throws CapabilityException {
		setState(State.INITIALIZED);
	}

	/**
	 * Activates this capability and change state to ACTIVE.
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void activate() throws CapabilityException {
		setState(State.ACTIVE);
	}

	/**
	 * Deactivate this capability and change state to INACTIVE
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void deactivate() throws CapabilityException {
		setState(State.INACTIVE);
	}

	/**
	 * Prepares capability for Garbage Collection state will be SHUTDOWN until it is collected.
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void shutdown() throws CapabilityException {
		setState(State.SHUTDOWN);
	}

	// IQueueingCapability methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.IQueueingCapability#sendRefreshActions()
	 */
	@Override
	public void sendRefreshActions() throws CapabilityException {
		sendRefreshActions(new ArrayList<Object>());
	}

	// TODO use when queue is in opennaas.core
	// @Override
	// public void queueuAction(IAction action) throws CapabilityException {
	// getQueuemanager().queueAction(action);
	// }uie
	//
	// /**
	// *
	// * @return
	// * @throws CapabilityException
	// */
	// protected abstract IQueueManagerCapability getQueueManager() throws CapabilityException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.IQueueingCapability#createAction(java.lang.String)
	 */
	@Override
	public IAction createAction(String actionId) throws CapabilityException {

		try {
			log.debug("Trying to use profile");
			IAction action = loadActionFromProfile(actionId);

			if (action == null) {
				log.debug("Profile doesn't contain desired action for this capability. Loading action from ActionSet. Action id is: " + actionId);
				ActionSet actionSet = (ActionSet) getActionSet();
				action = actionSet.obtainAction(actionId);
			}

			return action;

		} catch (ActionException e) {
			throw new CapabilityException(e);
		}
	}

	/**
	 * Sends to the queue necessary actions that should be executed before this capability is operative.
	 * 
	 * @param actionsParameters
	 *            A parameter will be taken from the list for each refreshActions. Fist action will get first parameter, second action the second...
	 *            null parameter will be passed if there are more actions than parameters. .
	 * @return
	 * @throws CapabilityException
	 */
	public void sendRefreshActions(List<Object> params) throws CapabilityException {

		List<String> refreshActions = getActionSet().getRefreshActionName();

		IAction action;
		int numAction = 0;
		for (String refreshAction : refreshActions) {
			Object param = null;
			if (numAction < params.size()) {
				param = params.get(numAction);
				numAction++;
			} else {
				param = null;
			}

			action = createActionAndCheckParams(refreshAction, param);
			queueAction(action);
		}
	}

	/**
	 * 
	 * @param actionId
	 * @param actionParameters
	 * @return created action with given parameters
	 * @throws CapabilityException
	 *             if an error occurs creating the action or checking it's parameters.
	 */
	protected IAction createActionAndCheckParams(String actionId, Object actionParameters) throws CapabilityException {

		IAction action = createAction(actionId);
		action.setParams(actionParameters);
		action.setModelToUpdate(resource.getModel());
		// FIXME define checkParams signature. Should it return exception if fails or return false???
		// now both Exception and return false have same meaning
		boolean isOk = action.checkParams(action.getParams());
		if (!isOk) {
			throw new CapabilityException("Invalid parameters for action " + actionId);
		}

		return action;
	}

	/**
	 * Register the capability like a web service through DOSGi
	 * 
	 * @param name
	 * @param resourceId
	 * @return
	 * @throws CapabilityException
	 */
	protected ServiceRegistration registerService(BundleContext bundleContext, String capabilityName, String resourceType, String resourceName,
			String ifaceName) throws CapabilityException {
		Dictionary<String, String> props = new Hashtable<String, String>();
		return registration = registerService(bundleContext, capabilityName, resourceType, resourceName, ifaceName, props);
	}

	/**
	 * Register the capability like a web service through DOSGi
	 * 
	 * @param name
	 * @param resourceId
	 * @return
	 * @throws CapabilityException
	 */
	protected ServiceRegistration registerService(BundleContext bundleContext, String capabilityName, String resourceType, String resourceName,
			String ifaceName, Dictionary<String, String> props) throws CapabilityException {
		// Rest
		if (props != null) {
			props.put("service.exported.interfaces", "*");
			props.put("service.exported.configs", "org.apache.cxf.rs");
			props.put("service.exported.intents", "HTTP");
			props.put("org.apache.cxf.ws.address", "http://localhost:8888/opennaas/" + resourceType + "/" + resourceName);
		}
		return registration = bundleContext.registerService(ifaceName, this, props);
	}

	/**
	 * 
	 * @return Action for this capability with given id stored in profile, or null if there is no such action in profile.
	 * @throws ActionException
	 *             if there is a problem instantiating the action
	 */
	private Action loadActionFromProfile(String actionId) throws ActionException {
		IProfile profile = resource.getProfile();

		ActionSet actionSet = null;
		Action action = null;

		if (profile != null) {
			actionSet = (ActionSet) profile.getActionSetForCapability(capabilityId);
			if (actionSet != null) {
				// try to load the actionId from profile ActionSet
				action = actionSet.obtainAction(actionId);
			}
		}
		return action;
	}

}
