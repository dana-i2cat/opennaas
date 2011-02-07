package net.i2cat.mantychore.core.resources.tests.capability;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.mantychore.core.resources.ILifecycle;
import net.i2cat.mantychore.core.resources.ResourceException;
import net.i2cat.mantychore.core.resources.capability.ICapability;
import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;
import net.i2cat.mantychore.core.resources.descriptor.Information;

public class CapabilityTest {
	private static Logger logger = LoggerFactory.getLogger(CapabilityTest.class);
	private static ICapability capability = null;
	
	@BeforeClass
	public static void setUp(){
		capability = new MockCapability(getMockCapabilityDescriptor());
	}
	
	private static CapabilityDescriptor getMockCapabilityDescriptor(){
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		Information information = new Information();
		information.setName("Mock capability");
		information.setType("Mock");
		information.setVersion("0.0.1");
		capabilityDescriptor.setCapabilityInformation(information);
		return capabilityDescriptor;
	}
	
	@Test
	public void printInfo(){
		logger.info(capability.toString());
	}
	
	@Test
	public void testInitialize() throws ResourceException{
		capability.initialize();
		Assert.assertEquals(ILifecycle.State.INITIALIZED, capability.getState());
		MockCapability mockCapability = (MockCapability) capability;
		Assert.assertEquals(mockCapability.getInternalCall(), "initialize");
	}
	
	@Test
	public void testActivate() throws ResourceException{
		capability.activate();
		Assert.assertEquals(ILifecycle.State.ACTIVE, capability.getState());
		MockCapability mockCapability = (MockCapability) capability;
		Assert.assertEquals(mockCapability.getInternalCall(), "activate");
	}
	
	@Test
	public void testDeactivate() throws ResourceException{
		capability.deactivate();
		Assert.assertEquals(ILifecycle.State.INACTIVE, capability.getState());
		MockCapability mockCapability = (MockCapability) capability;
		Assert.assertEquals(mockCapability.getInternalCall(), "deactivate");
	}
	
	@Test
	public void testShutdown() throws ResourceException{
		capability.shutdown();
		Assert.assertEquals(ILifecycle.State.SHUTDOWN, capability.getState());
		MockCapability mockCapability = (MockCapability) capability;
		Assert.assertEquals(mockCapability.getInternalCall(), "shutdown");
	}
}
