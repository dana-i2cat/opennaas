package net.i2cat.mantychore.queuemanager;

import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.helpers.MockActionFactory;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.queue.ModifyParams;
import net.i2cat.nexus.resources.queue.QueueConstants;

import org.junit.Assert;
import org.junit.Test;

public class QueueOperationsTest {

	@Test
	public void QueueAddRemoveTest() {
		CapabilityDescriptor queueDescriptor = ResourceDescriptorFactory.newCapabilityDescriptor("queue", "junos");
		QueueManager queueManager = new QueueManager(queueDescriptor);

		MockActionFactory actionFactory = new MockActionFactory();

		queueManager.queueAction(MockActionFactory.newMockActionOK("actionMock1"));
		queueManager.queueAction(MockActionFactory.newMockActionOK("actionMock2"));
		queueManager.queueAction(MockActionFactory.newMockActionOK("actionMock3"));
		Assert.assertTrue(queueManager.getActions().size() == 3);

		ModifyParams modifyParams = ModifyParams.newRemoveOperation(1);
		try {
			queueManager.sendMessage(QueueConstants.MODIFY, modifyParams);
		} catch (CapabilityException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(queueManager.getActions().size() == 2);
		Assert.assertTrue(queueManager.getActions().get(0).getActionID().equals("actionMock1"));
		Assert.assertTrue(queueManager.getActions().get(1).getActionID().equals("actionMock3"));

	}
}
