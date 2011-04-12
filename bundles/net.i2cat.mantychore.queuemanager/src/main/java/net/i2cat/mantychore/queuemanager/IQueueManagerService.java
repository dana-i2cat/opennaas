package net.i2cat.mantychore.queuemanager;

import java.util.List;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionException;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.resources.capability.CapabilityException;

public interface IQueueManagerService {

	public List<ActionResponse> execute() throws ProtocolException,
			CapabilityException, ActionException;

	public void empty();

	public void queueAction(Action action);

	public List<Action> getActions();

}
