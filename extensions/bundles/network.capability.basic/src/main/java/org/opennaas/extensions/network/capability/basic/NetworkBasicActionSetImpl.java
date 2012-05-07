package org.opennaas.extensions.network.capability.basic;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.IActionSet;

//FIXME this class should be in an other bundle!!!
public class NetworkBasicActionSetImpl implements IActionSet {

	private String	actionSetId;

	@Override
	public String getActionSetId() {
		return actionSetId;
	}

	@Override
	public void setActionSetId(String actionSetId) {
		this.actionSetId = actionSetId;
	}

	@Override
	public List<String> getActionNames() {
		return NetworkBasicActionSet.getActionNames();
	}

	@Override
	public List<String> getRefreshActionName() {
		return new ArrayList<String>();
	}

	@Override
	public Action obtainAction(String actionId) throws ActionException {
		throw new ActionException("Action not found");
	}

}
