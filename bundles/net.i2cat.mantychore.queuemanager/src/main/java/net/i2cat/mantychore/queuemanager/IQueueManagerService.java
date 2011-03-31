package net.i2cat.mantychore.queuemanager;

import java.util.List;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;

public interface IQueueManagerService {

	public List<ActionResponse> execute() throws ProtocolException,
			CommandException;

	public void empty();

	public void queueAction(Action action, Object params);

	public List<Action> getActions();

}
