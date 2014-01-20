package org.opennaas.extensions.queuemanager;

/*
 * #%L
 * OpenNaaS :: Queue Manager
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
