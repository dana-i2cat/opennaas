package net.i2cat.nexus.resources.capability;

import java.util.Vector;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionSet;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.profile.IProfile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides an abstract implementation for the ICapability interface. This class must be extended by each module and must implement the
 * abstract lifecycle methods.
 * 
 * @author Mathieu Lemay (ITI)
 * @author Scott Campbell(CRC)
 * @author Eduard Grasa (i2CAT)
 * @author Carlos Baez (i2CAT)
 * 
 */
public abstract class AbstractCapability implements ICapability {

	Log								log				= LogFactory.getLog(AbstractCapability.class);

	/** The descriptor for this capability **/
	protected CapabilityDescriptor	descriptor;
	protected State					state			= null;
	protected String				capabilityId	= null;
	protected IResource				resource		= null;

	protected IActionSet			actionSet		= null;

	protected IProfile				profile			= null;

	public AbstractCapability(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
		this.capabilityId = descriptor.getCapabilityInformation().getType();
		setState(State.INSTANTIATED);
	}

	public CapabilityDescriptor getCapabilityDescriptor() {
		return descriptor;
	}

	public Information getCapabilityInformation() {
		return descriptor.getCapabilityInformation();
	}

	/**
	 * The resource where this capability belongs
	 * 
	 * @param resource
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	/**
	 * Returns the current capability state
	 * 
	 * @return state enum object
	 */
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
	public void initialize() throws CapabilityException {
		initializeCapability();
		state = State.INITIALIZED;
	}

	/**
	 * Activates this capability and change state to ACTIVE.
	 * 
	 * @throws ResourceException
	 */
	public void activate() throws CapabilityException {
		activateCapability();
		state = State.ACTIVE;
	}

	/**
	 * Deactivate this capability and change state to INACTIVE
	 * 
	 * @throws ResourceException
	 */
	public void deactivate() throws CapabilityException {
		deactivateCapability();
		state = State.INACTIVE;
	}

	/**
	 * Prepares capability for Garbage Collection state will be SHUTDOWN until it is collected.
	 * 
	 * @throws ResourceException
	 */
	public void shutdown() throws CapabilityException {
		shutdownCapability();
		state = State.SHUTDOWN;
	}

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

	public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public Action createAction(String actionId) throws CapabilityException {

		try {
			Action action = null;
			log.debug("Trying to use profile");
			action = loadActionFromProfile(actionId);

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

	// It has not use setActionSets
	// public abstract void setActionSet(IActionSet actionSet);

	public abstract IActionSet getActionSet() throws CapabilityException;

	protected abstract void initializeCapability() throws CapabilityException;

	protected abstract void activateCapability() throws CapabilityException;

	protected abstract void deactivateCapability() throws CapabilityException;

	protected abstract void shutdownCapability() throws CapabilityException;

	/**
	 * Sends to queue the actions that should be executed before this capability is operative.
	 * 
	 * @return
	 */
	public Response sendStartUpActions() {
		try {

			Response response = null;

			if (getActionSet().getStartUpRefreshActionName() != null) {
				if (getActionSet().getActionNames().contains(getActionSet().getStartUpRefreshActionName())) {
					response = (Response) sendMessage(getActionSet().getStartUpRefreshActionName(), null);
				} else {
					Vector<String> errorMsgs = new Vector<String>();
					errorMsgs
							.add("Could not find action in capability actionset");
					response = Response.errorResponse(getActionSet().getStartUpRefreshActionName(), errorMsgs);
				}
			}
			return response;

		} catch (CapabilityException e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			return Response.errorResponse("STARTUP_REFRESH_ACTION", errorMsgs);
		}
	}

}
