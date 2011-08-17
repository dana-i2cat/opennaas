package org.opennaas.core.resources.command;

/**
 * Basic interface all Commands must implements
 * 
 * @author Evelyn Torras
 * 
 */
public interface ICommand {
	public String getCommandId();

	public void setCommandId(String commandId);

	public void setParams(Object params);
}
