package net.i2cat.nexus.resources.command;

public abstract class Command implements ICommand {
	private String	commandId;
	private Object	params;

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	// FIXME this methods are commond to all commands or should be into the implementation of the commands
	public abstract void initialize() throws CommandException;

}
