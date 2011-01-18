package net.i2cat.mantychore.queuemanager;

public abstract class Command {
	protected String	commandId;

	public abstract void initialize();

	public abstract Object message();

	public abstract void parseResponse(Object updatedModel);

}
