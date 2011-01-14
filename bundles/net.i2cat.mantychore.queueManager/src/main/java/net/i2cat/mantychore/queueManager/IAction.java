package net.i2cat.mantychore.queueManager;

import java.util.List;

import com.iaasframework.capabilities.commandset.ICommand;

public interface IAction {
	public List<ICommand> getCommands();
}
