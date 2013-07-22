package org.opennaas.extensions.router.opener.client.test;

import junit.framework.Assert;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.junit.Test;
import org.opennaas.extensions.router.opener.client.OpenerQuaggaOpenAPI;
import org.opennaas.extensions.router.opener.client.model.IPData;
import org.opennaas.extensions.router.opener.client.model.Interface;
import org.opennaas.extensions.router.opener.client.model.InterfaceID;
import org.opennaas.extensions.router.opener.client.rpc.AddInterfaceRequest;
import org.opennaas.extensions.router.opener.client.rpc.DeleteInterfaceIPRequest;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfacesResponse;
import org.opennaas.extensions.router.opener.client.rpc.IPRequest;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceIPRequest;

public class OpenerClientTest {
	
	private static final String host = "opener.opennaas.org";
	private static final String port = "9090";
	private static final String path = "axis2/services/quagga_openapi";
	
	private static final String managementInterface = "eth0";
	private static final String testInterface = "eth1";
	
	private static final String testIPAddress = "192.168.1.12";
	private static final String testIPPrefix = "24";
	
	
	@Test
	public void OpenerTest() throws Exception {
		
		OpenerQuaggaOpenAPI proxy = JAXRSClientFactory.create("http://"+host+":"+port+"/"+path, OpenerQuaggaOpenAPI.class);
		
		GetInterfacesResponse response = proxy.getInterfaces();
		
		for (String ifaceName : response.getInterfaces()){
			GetInterfaceResponse ifaceResponse = proxy.getInterface(ifaceName);
			Interface iface = ifaceResponse.getInterface();
			Assert.assertNotNull(iface);
		}
			
		IPRequest ipReq = new IPRequest();
		ipReq.setName(testInterface);
		IPData ip = new IPData();
		ip.setAddress(testIPAddress);
		ip.setPrefixLength(testIPPrefix);
		ipReq.setIp(ip);
		
		// DISABLED DUE TO ERROR IN OPENER
//		//set IP Address
//		SetInterfaceIPRequest setIPRequest = new SetInterfaceIPRequest();
//		setIPRequest.setIface(ipReq);
//		proxy.setInterfaceIPAddress(setIPRequest, 1);
		
		// DISABLED DUE TO ERROR IN OPENER
//		//remove IP Address
//		DeleteInterfaceIPRequest delIPRequest = new DeleteInterfaceIPRequest();
//		delIPRequest.setIface(ipReq);
//		proxy.deleteInterfaceIPAddress(delIPRequest);
		
		
		//create virtual interface
		InterfaceID vifaceId = new InterfaceID();
		vifaceId.setName(testInterface + ".1");
		
		// DISABLED DUE TO ERROR IN OPENER
//		AddInterfaceRequest addIfaceReq = new AddInterfaceRequest();
//		addIfaceReq.setId(vifaceId);
//		proxy.addInterface(addIfaceReq);
//		
		//set IP
		IPRequest ipReq2 = new IPRequest();
		ipReq2.setName(vifaceId.getName());
		ipReq2.setIp(ip);
	
		// DISABLED DUE TO ERROR IN OPENER
//		SetInterfaceIPRequest setIPRequest2 = new SetInterfaceIPRequest();
//		setIPRequest.setIface(ipReq2);
//		proxy.setInterfaceIPAddress(setIPRequest2, 1);
//		
		// DISABLED DUE TO ERROR IN OPENER
//		//remove IP
//		DeleteInterfaceIPRequest delIPRequest2 = new DeleteInterfaceIPRequest();
//		delIPRequest.setIface(ipReq2);
//		proxy.deleteInterfaceIPAddress(delIPRequest2);
		
		
		// NOT TESTED
//		// remove virtual iface
//		proxy.deleteInterface(vifaceId.getName());
		
	}

}
