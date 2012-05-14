package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.utils.ResourcesDemo;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.GreTunnelConfiguration;
import org.opennaas.ws.GreTunnelService;
import org.opennaas.ws.GreTunnelServiceConfiguration;
import org.opennaas.ws.IGRETunnelCapabilityService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.IpProtocolEndpoint;
import org.opennaas.ws.ProtocolException_Exception;
import org.opennaas.ws.ProvidesEndpoint;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class Step7Action extends ActionSupport implements SessionAware {

	private Map<String, Object>				session;
	private IGRETunnelCapabilityService		greTunnelService;
	private IQueueManagerCapabilityService	queueService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	private static final long	serialVersionUID	= 1L;

	/**
	 * shell:echo "CONFIGURE GRE TUNNELS" <br>
	 * ##configure vpn access using given interface through each router <br>
	 * gretunnel:create router:logicallola1 gr-1/2/0.1 192.168.1.17 255.255.255.252 193.1.190.250 84.88.40.26 <br>
	 * gretunnel:create router:logicalmyre1 gr-1/1/0.2 192.168.1.33 255.255.255.252 193.1.190.1 134.226.53.108 <br>
	 * queue:execute router:logicallola1 <br>
	 * queue:execute router:logicalmyre1 <br>
	 */
	@Override
	public String execute() throws Exception {
		configureGRE();
		return SUCCESS;
	}

	private void configureGRE() throws CapabilityException_Exception, ActionException_Exception, ProtocolException_Exception {
		greTunnelService = OpennaasClient.getGRETunnelCapabilityService();
		queueService = OpennaasClient.getQueueManagerCapabilityService();

		String lrLolaId = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_LOLA_NAME)).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_MYRE_NAME)).getId();

		greTunnelService.createGRETunnel(
				lrLolaId,
				getGRETunnel(ResourcesDemo.LOLA_IFACE_GRE, ResourcesDemo.LOLA_GRE_TUNNEL_IP, ResourcesDemo.IP_NET_MASK, ResourcesDemo.LOLA_IFACE1_IP,
						ResourcesDemo.LOLA_GRE_TUNNEL_DESTINY));
		greTunnelService.createGRETunnel(
				lrMyreId,
				getGRETunnel(ResourcesDemo.MYRE_IFACE_GRE, ResourcesDemo.MYRE_GRE_TUNNEL_IP, ResourcesDemo.IP_NET_MASK, ResourcesDemo.MYRE_IFACE1_IP,
						ResourcesDemo.MYRE_GRE_TUNNEL_DESTINY));

		queueService.execute(lrLolaId);
		queueService.execute(lrMyreId);
	}

	private GreTunnelService getGRETunnel(String interfaceName, String tunnelIP, String tunnelMask, String ipSource, String ipDestiny) {
		GreTunnelService greService = new GreTunnelService();
		greService.setName(interfaceName);

		GreTunnelConfiguration greConfig = new GreTunnelConfiguration();
		greConfig.setSourceAddress(ipSource);
		greConfig.setDestinationAddress(ipDestiny);

		IpProtocolEndpoint ipEndPoint = new IpProtocolEndpoint();
		ipEndPoint.setIPv4Address(tunnelIP);
		ipEndPoint.setSubnetMask(tunnelMask);

		// associate GRETunnelService and GRETunnelConfiguration
		GreTunnelServiceConfiguration assoc = new GreTunnelServiceConfiguration();
		assoc.setTo(greConfig);
		greService.getToAssociations().add(assoc);

		// associate GRETunnelService and GRETunnelEndpoint
		ProvidesEndpoint assoc2 = new ProvidesEndpoint();
		assoc2.setTo(ipEndPoint);
		greService.getToAssociations().add(assoc2);

		return greService;
	}
}
