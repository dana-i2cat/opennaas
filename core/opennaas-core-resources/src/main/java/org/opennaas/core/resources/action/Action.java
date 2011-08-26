package org.opennaas.core.resources.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;

public abstract class Action implements IAction {

	Log							log				= LogFactory
														.getLog(Action.class);

	// FIXME what model is the entire model or the one for change from the command
	protected IModel	modelToUpdate;
	protected Object			params			= null;
	protected String			actionID		= null;

	protected Object			behaviorParams	= null;

	public IModel getModelToUpdate() {
		return modelToUpdate;
	}

	public void setModelToUpdate(IModel modelToUpdate) {
		this.modelToUpdate = modelToUpdate;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public String getActionID() {
		return actionID;
	}

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
