package net.i2cat.nexus.resources.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@SuppressWarnings({ "serial", "unchecked" })
public class ActionSet implements IActionSet {
	public String								actionsetId		= null;

	HashMap<String, Class<? extends Action>>	actions			= new HashMap<String, Class<? extends Action>>();
	HashMap<String, Properties>					actionParams	= new HashMap<String, Properties>();

	public Class<? extends Action> putAction(String key, Class<? extends Action> value) {
		return actions.put(key, value);
	}

	public Properties putActionParams(String key, Properties value) {
		return actionParams.put(key, value);
	}

	public Class<? extends Action> getAction(String actionId) {
		return actions.get(actionId);
	}

	public Properties getActionParams(String actionId) {
		return actionParams.get(actionId);
	}

	@Override
	public Action obtainAction(String actionId) throws ActionException {
		Class<? extends Action> actionClass = actions.get(actionId);
		Action action = null;
		if (actionClass != null) {
			try {
				action = actionClass.newInstance();

				if (action != null) {
					if (actionParams.get(actionId) != null) {
						action.setBehaviorParams(actionParams.get(actionId));
					}
				}
			} catch (InstantiationException e) {
				// TODO write ActionException message actionID
				throw new ActionException("", e);
			} catch (IllegalAccessException e) {
				// TODO write ActionException message actionID
				throw new ActionException("", e);
			}
		}
		return action;
	}

	@Override
	public List<String> getActionNames() {
		ArrayList<String> names = new ArrayList<String>();
		names.addAll(actions.keySet());
		return names;
	}

	@Override
	public String getActionSetId() {
		return actionsetId;
	}

	@Override
	public void setActionSetId(String actionSetId) {
		this.actionsetId = actionSetId;
	}

}
