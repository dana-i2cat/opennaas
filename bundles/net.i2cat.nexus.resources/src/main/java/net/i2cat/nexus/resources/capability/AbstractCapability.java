package net.i2cat.nexus.resources.capability;

import java.util.ArrayList;
import java.util.List;
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

	@Override
	public CapabilityDescriptor getCapabilityDescriptor() {
		return descriptor;
	}

	@Override
	public Information getCapabilityInformation() {
		return descriptor.getCapabilityInformation();
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
	@Override
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
		initializeCapability();
		state = State.INITIALIZED;
	}

	/**
	 * Activates this capability and change state to ACTIVE.
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void activate() throws CapabilityException {
		activateCapability();
		state = State.ACTIVE;
	}

	/**
	 * Deactivate this capability and change state to INACTIVE
	 * 
	 * @throws ResourceException
	 */
	@Override
	public void deactivate() throws CapabilityException {
		deactivateCapability();
		state = State.INACTIVE;
	}

	/**
	 * Prepares capability for Garbage Collection state will be SHUTDOWN until it is collected.
	 * 
	 * @throws ResourceException
	 */
	@Override
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

	@Override
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
	 * Method if it is not necessasry to use params to send Refresh Actions
	 * */
	public Response sendRefreshActions() {
		return this.sendRefreshActions(new ArrayList());
	}

	/**
	 * Sends to the queue necessary actions that should be executed before this capability is operative.
	 * 
	 * @return
	 */
	public Response sendRefreshActions(List params) {
		try {

			List<String> refreshActions = getActionSet().getRefreshActionName();

			// TODO TO IMPROVE
			// this.resource.setModel(null);

			List<Response> responses = new ArrayList<Response>();
			int numAction = 1;
			for (String refreshAction : refreshActions) {

				Object param = null;
				// Check if it exists an available action
				if (params.size() >= numAction)
					param = params.get(numAction - 1);

				responses.add((Response) sendMessage(refreshAction, param));
			}

			return prepareResponse(responses);

		} catch (CapabilityException e) {
			return prepareErrorMessage("STARTUP_REFRESH_ACTION", e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
		}

	}

	private static final String	REFRESH	= "refresh";

	private Response prepareResponse(List<Response> responses) {
		Response responseRefresh = Response.okResponse(REFRESH);
		Vector<String> errors = new Vector<String>();
		for (Response response : responses) {
			if (response.getStatus() == Response.Status.ERROR) {
				errors.add(concatenateList(response.getErrors(), "\n"));
			}
		}
		if (!errors.isEmpty()) {
			responseRefresh = Response.errorResponse(REFRESH, errors);
		}

		return responseRefresh;
	}

	private Response prepareErrorMessage(String nameError, String message) {
		Vector<String> errorMsgs = new Vector<String>();
		errorMsgs.add(message);
		return Response.errorResponse(nameError, errorMsgs);

	}

	private static String concatenateList(List<String> listStrings, String separator) {
		String modifiedString = "";
		String lastElement = listStrings.remove(listStrings.size() - 1);
		for (String str : listStrings) {
			modifiedString.concat(str + separator);
		}
		modifiedString.concat(lastElement);
		return modifiedString;
	}

}
