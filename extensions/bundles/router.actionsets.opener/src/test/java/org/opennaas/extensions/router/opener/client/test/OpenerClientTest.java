package org.opennaas.extensions.router.opener.client.test;

import junit.framework.Assert;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.router.opener.client.OpenerQuaggaOpenAPI;
import org.opennaas.extensions.router.opener.client.model.Interface;
import org.opennaas.extensions.router.opener.client.rpc.DeleteInterfaceIPRequest;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfacesResponse;
import org.opennaas.extensions.router.opener.client.rpc.IPDataRequest;
import org.opennaas.extensions.router.opener.client.rpc.IPRequest;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceIPRequest;
import org.opennaas.extensions.router.opener.protocol.OpenerProtocolSession;
import org.opennaas.extensions.router.opener.protocol.OpenerProtocolSessionFactory;

public class OpenerClientTest {
	
	private static final String host = "opener.opennaas.org";
	private static final String port = "9090";
	private static final String path = "axis2/services/quagga_openapi/linux";
	
	private static final String managementInterface = "eth0";
	private static final String testInterface = "eth1";
	
	private static final String testIPAddress = "192.168.1.12";
	private static final String testIPPrefix = "24";
	
	@Ignore
	@Test
	public void openerClientTest() throws Exception {
		
		OpenerQuaggaOpenAPI proxy = JAXRSClientFactory.create("http://"+host+":"+port+"/"+path, OpenerQuaggaOpenAPI.class);
		
		GetInterfacesResponse response = proxy.getInterfaces();
		
		for (String ifaceName : response.getInterfaces()){
			GetInterfaceResponse ifaceResponse = proxy.getInterface(ifaceName);
			Interface iface = ifaceResponse.getInterface();
			Assert.assertNotNull(iface);
		}
			
		IPRequest ipReq = new IPRequest();
		ipReq.setName(testInterface);
		IPDataRequest ip = new IPDataRequest();
		ip.setAddress(testIPAddress);
		ip.setPrefixLength(testIPPrefix);
		ipReq.setIp(ip);
		
		
		//set IP Address
		SetInterfaceIPRequest setIPRequest = new SetInterfaceIPRequest();
		setIPRequest.setIface(ipReq);
		proxy.setInterfaceIPAddress(setIPRequest, 0);
		
		
		//remove IP Address
		DeleteInterfaceIPRequest delIPRequest = new DeleteInterfaceIPRequest();
		delIPRequest.setIface(ipReq);
		proxy.deleteInterfaceIPAddress(delIPRequest);
		
		
//		//create virtual interface
//		InterfaceID vifaceId = new InterfaceID();
//		vifaceId.setName(testInterface + ".1");
//		
//		// DISABLED DUE TO ERROR IN OPENER
//		AddInterfaceRequest addIfaceReq = new AddInterfaceRequest();
//		addIfaceReq.setId(vifaceId);
//		proxy.addInterface(addIfaceReq);
//		
//		//set IP
//		IPRequest ipReq2 = new IPRequest();
//		ipReq2.setName(vifaceId.getName());
//		ipReq2.setIp(ip);
//	
//		SetInterfaceIPRequest setIPRequest2 = new SetInterfaceIPRequest();
//		setIPRequest.setIface(ipReq2);
//		proxy.setInterfaceIPAddress(setIPRequest2, 1);
//		
//		//remove IP
//		DeleteInterfaceIPRequest delIPRequest2 = new DeleteInterfaceIPRequest();
//		delIPRequest.setIface(ipReq2);
//		proxy.deleteInterfaceIPAddress(delIPRequest2);
//		
//		
//		// NOT TESTED
//		// remove virtual iface
//		proxy.deleteInterface(vifaceId.getName());
		
	}
	
	@Ignore
	@Test
	public void openerProtocolSessionTest() throws Exception {
		
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, OpenerProtocolSession.OPENER_PROTOCOL_TYPE);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "http://"+host+":"+port+"/"+path);
		
		OpenerProtocolSessionFactory factory = new OpenerProtocolSessionFactory();
		OpenerProtocolSession session = (OpenerProtocolSession) factory.createProtocolSession("OPENER-SESSION-001", protocolSessionContext);
		
		session.connect();
		OpenerQuaggaOpenAPI openerClient = session.getOpenerClientForUse();
		GetInterfacesResponse response = openerClient.getInterfaces();
		Assert.assertNotNull(response);
		
		
		session.disconnect();
		
	}
	

}
