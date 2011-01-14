package net.i2cat.mantychore.queueManager;

import org.junit.Test;

import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;

public class QueueManagerCapabilityTest {

	@Test
	public void blabla()
	{
		CapabilityDescriptor descriptor = new CapabilityDescriptor();
		
		QueueManagerCapability queueCapability = new QueueManagerCapability(descriptor,"resource");
		
		queueCapability.queueAction( new IAction(){} );
	}
}
