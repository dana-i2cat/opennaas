package net.i2cat.mantychore.commons;

public abstract class Command {
	protected String	commandId;

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public abstract void initialize();

	public abstract Object message();

	public abstract void parseResponse(Object response, Object model);

}
