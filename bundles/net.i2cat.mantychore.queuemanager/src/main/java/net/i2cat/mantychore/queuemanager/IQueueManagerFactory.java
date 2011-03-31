package net.i2cat.mantychore.queuemanager;

import java.util.List;

import net.i2cat.nexus.resources.IResource;

public interface IQueueManagerFactory {
	public QueueManager createQueueManager(List<String> actionIds,
			IResource resource);
}
