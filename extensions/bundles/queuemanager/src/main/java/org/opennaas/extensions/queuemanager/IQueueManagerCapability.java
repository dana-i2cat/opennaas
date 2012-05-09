package org.opennaas.extensions.queuemanager;

import java.util.List;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueResponse;

public interface IQueueManagerCapability extends ICapability {

	// This method is for tests
	public QueueResponse execute() throws ProtocolException,
			CapabilityException, ActionException;

	public void clear();

	public void queueAction(IAction action) throws CapabilityException;

	public List<IAction> getActions();

	public void modify(ModifyParams modifyParams) throws CapabilityException;
}
