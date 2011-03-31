package net.i2cat.mantychore.queuemanager;

import java.util.List;

import net.i2cat.nexus.resources.IResource;

public class QueueManagerFactory implements IQueueManagerFactory {

	// TODO TO FIX, HOW WE CAN CALL ICAPABITIES OR QUEUEMANAGER
	public QueueManager createQueueManager(List<String> actionIds,
			IResource resource) {
		return new QueueManager(actionIds, resource);
	}

}
