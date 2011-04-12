package net.i2cat.mantychore.commons;

import java.util.List;

public interface IActionSetFactory {
	public Action createAction(String actionId) throws ActionException;

	public List<String> getActionNames();

}
