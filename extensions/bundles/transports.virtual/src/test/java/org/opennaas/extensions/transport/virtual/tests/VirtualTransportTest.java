package org.opennaas.extensions.transport.virtual.tests;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.extensions.transports.virtual.VirtualTransport;

public class VirtualTransportTest {
	
	private static VirtualTransport virtualTransport = null;
	
	@BeforeClass
	public static void setUp(){
		try{
			virtualTransport = new VirtualTransport(new MockVirtualTransportProvider());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testStreamTransport(){
		String request = "ACT-USER::admin:::opterasm";
		try {
			virtualTransport.send(request.toCharArray());
			Assert.assertEquals(18, virtualTransport.getInputStream().available());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(null);
		}
	}
	
	@Test
	public void testMessageTransport(){
		String request = "ACT-USER::admin:::opterasm";
		try {
			Object response = virtualTransport.sendMessage(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(null);
		}
	}

}
