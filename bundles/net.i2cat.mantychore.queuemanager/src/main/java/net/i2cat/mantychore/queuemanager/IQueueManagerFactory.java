package net.i2cat.mantychore.queuemanager;

public interface IQueueManagerFactory {
	public QueueManager createQueueManager(String resourceId);
}
