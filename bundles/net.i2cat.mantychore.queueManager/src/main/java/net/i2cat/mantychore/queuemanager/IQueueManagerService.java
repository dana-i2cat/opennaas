package net.i2cat.mantychore.queuemanager;

import java.util.List;

import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;

public interface IQueueManagerService {

	public void execute() throws ProtocolException;

	public void empty();

	public void queueAction(Action action);

	public List<Action> getActions();

}
