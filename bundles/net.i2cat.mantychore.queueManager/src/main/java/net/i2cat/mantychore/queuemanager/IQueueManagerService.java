package net.i2cat.mantychore.queuemanager;

import java.util.List;

import com.iaasframework.protocolsessionmanager.ProtocolException;

public interface IQueueManagerService {

	public void execute() throws ProtocolException;

	public void empty();

	public void queueAction(Action action);

	public List<Action> getActions();

}
