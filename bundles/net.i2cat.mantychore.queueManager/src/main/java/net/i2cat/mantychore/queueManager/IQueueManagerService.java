package net.i2cat.mantychore.queueManager;

import java.util.List;

public interface IQueueManagerService {

	public void queueAction(IAction action);
	
	public void execute();
	
	public void empty();
	
	public List<IAction> getActions();
}
