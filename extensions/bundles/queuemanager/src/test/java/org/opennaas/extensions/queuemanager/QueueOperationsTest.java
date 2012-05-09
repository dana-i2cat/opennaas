package org.opennaas.extensions.queuemanager;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockActionFactory;
import org.opennaas.core.resources.queue.ModifyParams;

public class QueueOperationsTest {

	@Test
	public void QueueAddRemoveTest() throws CapabilityException {
		CapabilityDescriptor queueDescriptor = ResourceDescriptorFactory.newCapabilityDescriptor("queue", "junos");
		QueueManager queueManager = new QueueManager(queueDescriptor);

		MockActionFactory actionFactory = new MockActionFactory();

		queueManager.queueAction(MockActionFactory.newMockActionOK("actionMock1"));
		queueManager.queueAction(MockActionFactory.newMockActionOK("actionMock2"));
		queueManager.queueAction(MockActionFactory.newMockActionOK("actionMock3"));
		Assert.assertTrue(queueManager.getActions().size() == 3);

		ModifyParams modifyParams = ModifyParams.newRemoveOperation(1);
		queueManager.modify(modifyParams);
		Assert.assertTrue(queueManager.getActions().size() == 2);
		Assert.assertTrue(queueManager.getActions().get(0).getActionID().equals("actionMock1"));
		Assert.assertTrue(queueManager.getActions().get(1).getActionID().equals("actionMock3"));
	}
}
