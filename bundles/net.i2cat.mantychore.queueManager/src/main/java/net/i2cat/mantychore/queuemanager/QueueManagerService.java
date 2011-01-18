package net.i2cat.mantychore.queuemanager;

import java.util.List;

public interface QueueManagerService {

	public void execute();

	public void empty();

	public void queueAction(Action action);

	public List<Action> getActions();

}
