package net.i2cat.nexus.resources.action;

import net.i2cat.mantychore.model.ManagedElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Action implements IAction {

	Log							log				= LogFactory
														.getLog(Action.class);

	// FIXME what model is the entire model or the one for change from the command
	protected ManagedElement	modelToUpdate;
	protected Object			params			= null;
	protected String			actionID		= null;

	protected Object			behaviorParams	= null;

	@Override
	public ManagedElement getModelToUpdate() {
		return modelToUpdate;
	}

	@Override
	public void setModelToUpdate(ManagedElement modelToUpdate) {
		this.modelToUpdate = modelToUpdate;
	}

	@Override
	public Object getParams() {
		return params;
	}

	@Override
	public void setParams(Object params) {
		this.params = params;
	}

	@Override
	public String getActionID() {
		return actionID;
	}

	@Override
	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	public Object getBehaviorParams() {
		return behaviorParams;
	}

	/**
	 * Set params an action may use to specify its behavior (i.e. path to a template file to be loaded)
	 * 
	 * @param params
	 */
	public void setBehaviorParams(Object params) {
		behaviorParams = params;
	}

}
