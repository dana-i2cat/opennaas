package com.iaasframework.capabilities.protocol.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.iaasframework.capabilities.protocol.IProtocolConstants;
import com.iaasframework.capabilities.protocol.IProtocolSessionFactory;
import com.iaasframework.capabilities.protocol.ProtocolCapability;
import com.iaasframework.resources.core.ILifecycle.State;
import com.iaasframework.resources.core.capability.CapabilityException;
import com.iaasframework.resources.core.descriptor.CapabilityDescriptor;
import com.iaasframework.resources.core.descriptor.Information;

public class ProtocolCapabilityTest {

	/** The class under test **/
	private ProtocolCapability protocolCapability = null;
	private IProtocolSessionFactory protocolSessionFactory = null;
	
	/** Contains the information required to instantiate and initialize a ProtocolCapability **/
	private CapabilityDescriptor capabilityDescriptor = null;
	
	private String resourceId = "resource123";
	
	@Before
	public void setup(){
		Information information = new Information();
		information.setName(IProtocolConstants.PROTOCOL + " capability");
		information.setType(IProtocolConstants.PROTOCOL);
		information.setVersion("1.0.0");
		capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityInformation(information);
		
		protocolCapability = new ProtocolCapability(capabilityDescriptor, resourceId);
	}
	
	@Test
	@Ignore
	public void testInitializeProtocolSessionManager() throws CapabilityException{
		protocolCapability.initialize();
		assertEquals(protocolCapability.getState(), State.INITIALIZED);
	}
}