package net.i2cat.mantychore.queuemanager;

import java.util.List;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;

public interface IQueueManagerService {

	public void execute() throws ProtocolException;

	public void empty();

	public void queueAction(Action action, ProtocolSessionContext protocol);

	public List<Action> getActions();

}
