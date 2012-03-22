package net.i2cat.mantychore.queuemanager;

import java.util.List;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;

public interface IQueueManagerService {

	// This method is for tests
	public QueueResponse execute() throws ProtocolException,
			CapabilityException, ActionException;

	public void empty();

	public void queueAction(IAction action);

	public List<IAction> getActions();

}
