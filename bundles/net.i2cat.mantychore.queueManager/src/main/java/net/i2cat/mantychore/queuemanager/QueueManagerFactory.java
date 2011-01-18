package net.i2cat.mantychore.queuemanager;

public class QueueManagerFactory implements IQueueManagerFactory {

	public QueueManager createQueueManager(String resourceId) {
		// TODO Auto-generated method stub
		return new QueueManager(resourceId);
	}

}
