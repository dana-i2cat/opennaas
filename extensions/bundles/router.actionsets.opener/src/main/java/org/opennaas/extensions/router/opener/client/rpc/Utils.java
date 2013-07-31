package org.opennaas.extensions.router.opener.client.rpc;

public class Utils {
	
	public static SetInterfaceIPRequest createSetInterfaceIPRequest(String ifaceName, String ipAddress, String ipAddressPrefixLength) {
		
		IPDataRequest ip = new IPDataRequest();
		ip.setAddress(ipAddress);
		ip.setPrefixLength(ipAddressPrefixLength);
		
		IPRequest ifaceIPConfig = new IPRequest();
		ifaceIPConfig.setName(ifaceName);
		ifaceIPConfig.setIp(ip);
		
		SetInterfaceIPRequest request = new SetInterfaceIPRequest();
		request.setIface(ifaceIPConfig);
		
		return request;
	}

}
