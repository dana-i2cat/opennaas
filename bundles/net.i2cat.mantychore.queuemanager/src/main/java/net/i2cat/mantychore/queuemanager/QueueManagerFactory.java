package net.i2cat.mantychore.queuemanager;

public class QueueManagerFactory implements IQueueManagerFactory {

	
	//TODO TO FIX, HOW WE CAN CALL ICAPABITIES OR QUEUEMANAGER
	public QueueManager createQueueManager(String resourceId) {
		// TODO Auto-generated method stub
		return new QueueManager(resourceId);
	}

}
