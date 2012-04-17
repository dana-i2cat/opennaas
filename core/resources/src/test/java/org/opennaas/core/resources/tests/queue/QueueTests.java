package org.opennaas.core.resources.tests.queue;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockAction;
import org.opennaas.core.resources.mock.MockActionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueCapability;
import org.opennaas.core.resources.queue.QueueResponse;

public class QueueTests {

	private final Log			log	= LogFactory.getLog(QueueTests.class);

	static CapabilityDescriptor	queueDescriptor;
	static MockActionFactory	actionFactory;
	static QueueCapability		queue;

	@BeforeClass
	public static void setup() throws CapabilityException
	{
		queueDescriptor = ResourceDescriptorFactory.newCapabilityDescriptor("queue", "junos");

		queue = new QueueCapability(queueDescriptor);
		queue.initialize();

		actionFactory = new MockActionFactory();
	}

	@Before
	public void before() throws CapabilityException
	{
		queue.activate();
	}

	@After
	public void after() throws CapabilityException
	{
		queue.deactivate();
	}

	@AfterClass
	public static void shutdown() throws CapabilityException
	{
		queue.shutdown();
	}

	@Test
	public void oneOk() throws ActionException, CapabilityException, ProtocolException {

		assertEquals(queue.getActions().size(), 0);

		queue.queueAction(MockActionFactory.newMockActionOK("actionMock1"));

		assertEquals(queue.getActions().size(), 1);

		queue.execute();

		assertEquals(queue.getActions().size(), 0);
	}

	@Test
	public void empty() throws ActionException, CapabilityException, ProtocolException
	{
		assertEquals(queue.getActions().size(), 0);
		queue.execute();
		assertEquals(queue.getActions().size(), 0);
	}

	@Test
	public void threeOk() throws ActionException, CapabilityException, ProtocolException {

		assertEquals(queue.getActions().size(), 0);

		queue.queueAction(MockActionFactory.newMockActionOK("actionMock1"));
		queue.queueAction(MockActionFactory.newMockActionOK("actionMock2"));
		queue.queueAction(MockActionFactory.newMockActionOK("actionMock3"));

		assertEquals(queue.getActions().size(), 3);
		assertEquals(queue.getActions().get(0).getActionID(), "actionMock1");
		assertEquals(queue.getActions().get(1).getActionID(), "actionMock2");
		assertEquals(queue.getActions().get(2).getActionID(), "actionMock3");

		queue.execute();

		assertEquals(queue.getActions().size(), 0);
	}

	@Test
	public void threeOneFail() throws ActionException, CapabilityException, ProtocolException {

		assertEquals(queue.getActions().size(), 0);

		queue.queueAction(MockActionFactory.newMockActionOK("actionMock1"));
		queue.queueAction(MockActionFactory.newMockActionOK("actionMock2"));
		queue.queueAction(MockActionFactory.newMockActionAnError("actionMockError"));
		queue.queueAction(MockActionFactory.newMockActionOK("actionMock4"));

		assertEquals(queue.getActions().size(), 4);
		assertEquals(queue.getActions().get(0).getActionID(), "actionMock1");
		assertEquals(queue.getActions().get(1).getActionID(), "actionMock2");
		assertEquals(queue.getActions().get(2).getActionID(), "actionMockError");
		assertEquals(queue.getActions().get(3).getActionID(), "actionMock4");

		QueueResponse response = queue.execute();

		assertFalse(response.isOk());

		// on fail, the queue is not emptied.
		assertEquals(queue.getActions().size(), 4);

		assertTrue(((MockAction) queue.getActions().get(0)).isExecuted());
		assertTrue(((MockAction) queue.getActions().get(1)).isExecuted());
		assertTrue(((MockAction) queue.getActions().get(2)).isExecuted());
		assertFalse(((MockAction) queue.getActions().get(3)).isExecuted());
	}

	@Test
	public void threeOneFailWithException() throws ActionException, CapabilityException, ProtocolException {

		assertEquals(queue.getActions().size(), 0);

		queue.queueAction(MockActionFactory.newMockActionOK("actionMock1"));
		queue.queueAction(MockActionFactory.newMockActionOK("actionMock2"));
		queue.queueAction(MockActionFactory.newMockActionExceptionOnExecute("actionMockErrorException"));
		queue.queueAction(MockActionFactory.newMockActionOK("actionMock4"));

		assertEquals(queue.getActions().size(), 4);
		assertEquals(queue.getActions().get(0).getActionID(), "actionMock1");
		assertEquals(queue.getActions().get(1).getActionID(), "actionMock2");
		assertEquals(queue.getActions().get(2).getActionID(), "actionMockErrorException");
		assertEquals(queue.getActions().get(3).getActionID(), "actionMock4");

		QueueResponse response = queue.execute();

		assertFalse(response.isOk());

		// on fail, the queue is not emptied.
		assertEquals(queue.getActions().size(), 4);

		assertTrue(((MockAction) queue.getActions().get(0)).isExecuted());
		assertTrue(((MockAction) queue.getActions().get(1)).isExecuted());
		assertTrue(((MockAction) queue.getActions().get(2)).isExecuted());
		assertFalse(((MockAction) queue.getActions().get(3)).isExecuted());
	}
}
