package net.i2cat.mantychore.queuemanager;

import java.util.List;

import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.queue.QueueResponse;

public interface IQueueManagerService {

	// This method is for tests
	public QueueResponse execute() throws ProtocolException,
			CapabilityException, ActionException;

	public void empty();

	public void queueAction(IAction action);

	public List<IAction> getActions();

}
