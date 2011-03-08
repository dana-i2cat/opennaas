package net.i2cat.mantychore.commons;

import java.util.List;


public interface IActionSetFactory {
	public Action createAction(String actionId);

	public List<String> getActionNames();

}
