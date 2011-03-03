package net.i2cat.mantychore.actionsets.junos;

import java.util.List;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

public interface IActionSetService {

	public List<ActionResponse> execute() throws ProtocolException, CommandException;

	public void empty();

	public void queueAction(Action action, ProtocolSessionContext protocol);

	public List<Action> getActions();

}
