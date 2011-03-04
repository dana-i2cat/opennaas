package net.i2cat.mantychore.actionsets.junos;

import java.util.List;

import net.i2cat.mantychore.commons.Action;

public interface IActionSetFactory {
	public Action createAction(String actionId, Object params);

	public List<String> getActionNames();

}
