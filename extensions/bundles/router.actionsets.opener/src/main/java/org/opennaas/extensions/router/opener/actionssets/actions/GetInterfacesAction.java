package org.opennaas.extensions.router.opener.actionssets.actions;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperatingStatus;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.LogicalPort.PortType;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.opener.actionssets.OpenerActionSetConstants;
import org.opennaas.extensions.router.opener.client.OpenerQuaggaOpenAPI;
import org.opennaas.extensions.router.opener.client.model.IPData;
import org.opennaas.extensions.router.opener.client.model.Interface;


public class GetInterfacesAction extends OpenerAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException {
		
		OpenerQuaggaOpenAPI openerClient;
		try {
			openerClient = getOpenerProtocolSession(protocolSessionManager).getOpenerClientForUse();
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
		
		try {
		
			List<String> ifaceNames = openerClient.getInterfaces().getInterfaces();
			List<Interface> ifaces = new ArrayList<Interface>(ifaceNames.size());

			for (String ifaceName : ifaceNames) {
				
				Interface iface = openerClient.getInterface(ifaceName).getInterface();
				// WORKARROUND FOR AN EXISTING BUG:
				// getInterface does not return interface name correctly
				iface.setName(ifaceName);
				
				ifaces.add(iface);
			}
			
			parseIfaces(ifaces);
			
			return ActionResponse.okResponse(OpenerActionSetConstants.REFRESH_ACTION_OPENER);
			
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}
	
	private void parseIfaces(List<Interface> ifaces) {
		
		ComputerSystem router = (ComputerSystem) modelToUpdate;
		// TODO implements a better method to merge the elements in model
		// now are deleted all the existing elements this method creates
		// before adding new ones
		removeAllInterfaces(router);
		
		for (Interface iface : ifaces) {
			
			//parse interface
			NetworkPort port;
			if (iface.getName()!= null && iface.getName().startsWith("eth")) {
				port = new EthernetPort();
				port.setLinkTechnology(LinkTechnology.ETHERNET);
			} else {
				port = new NetworkPort();
			}
			
			port.setName(getPortName(iface));
			port.setPortNumber(getPortNumber(iface));
			port.setOperationalStatus(getOperationalStatus(iface));
			
			if (iface.getMtu() != null && !iface.getMtu().isEmpty())
				port.setActiveMaximumTransmissionUnit(Long.parseLong(iface.getMtu()));
			
			if (iface.getHardwareAddress() != null) {
				//FIXME iface.getHardwareAddress() must be encoded matching EthernetPort#setNetworkAddresses(String[]) 
				// when port in an EthernetPort
				String[] macAddresses = {iface.getHardwareAddress()};
				port.setNetworkAddresses(macAddresses); 
			}
			
			if (iface.getIp() != null)
				port.addProtocolEndpoint(parseIPData(iface.getIp()));
			if (iface.getIp6() != null)
				port.addProtocolEndpoint(parseIPData(iface.getIp6()));
			
			router.addLogicalDevice(port);
		}
	}
	
	private ComputerSystem removeAllInterfaces(ComputerSystem router) {
		router.removeAllLogicalDeviceByType(NetworkPort.class);
		return router;
	}
	
	private String getPortName(Interface iface) {
		if(iface.getName() == null || iface.getName().isEmpty())
			return "";
		
		String[] nameAndUnit = iface.getName().split("\\.");
		return nameAndUnit[0];
	}
	
	private int getPortNumber(Interface iface) {
		if(iface.getName() == null || iface.getName().isEmpty())
			return 0;
		
		String[] nameAndUnit = iface.getName().split("\\.");
		if (nameAndUnit.length < 2)
			return 0;
		
		return Integer.parseInt(nameAndUnit[1]);
	}
	
	private OperationalStatus getOperationalStatus(Interface iface) {
		
		OperationalStatus opStatus;
		if (iface.getStatus() == null) {
			opStatus = OperationalStatus.UNKNOWN;
		} else {
			if (iface.getStatus().equals("up")) {
				opStatus = OperationalStatus.OK;
			} else if (iface.getStatus().equals("down")) {
				opStatus = OperationalStatus.STOPPED;
			} else {
				opStatus = OperationalStatus.UNKNOWN;
			}
		}
		
		return opStatus;
	}
	
	private IPProtocolEndpoint parseIPData (IPData ipData) {
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		if (ipData.getFamilyType() != null && ipData.getFamilyType().equals("inet6")) {
			ip.setProtocolIFType(ProtocolIFType.IPV6);
			ip.setIPv6Address(ipData.getAddress());
			if (ipData.getPrefixLength() != null && ! ipData.getPrefixLength().isEmpty())
				ip.setPrefixLength(Short.parseShort(ipData.getPrefixLength()));
		} else {
			ip.setProtocolIFType(ProtocolIFType.IPV4);
			ip.setIPv4Address(ipData.getAddress());
			if (ipData.getPrefixLength() != null && ! ipData.getPrefixLength().isEmpty())
				ip.setSubnetMask(IPUtilsHelper.parseShortToLongIpv4NetMask(ipData.getPrefixLength()));
		}
		
		return ip;
	}

}
