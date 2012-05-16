package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
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
public class ConfigureGRETunnelAction extends ActionSupport implements SessionAware {

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
	 * gretunnel:create router:logicalunic1 gr-1/2/0.1 192.168.1.17 255.255.255.252 193.1.190.250 84.88.40.26 <br>
	 * gretunnel:create router:logicalmyre1 gr-1/1/0.2 192.168.1.33 255.255.255.252 193.1.190.1 134.226.53.108 <br>
	 * queue:execute router:logicalunic1 <br>
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

		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();

		greTunnelService.createGRETunnel(
				lrMyreId,
				getGRETunnel(getText("myre.iface.gre"), getText("myre.gretunnel.ip"), getText("common.ip.mask"), getText("myre.iface1.ip"),
						getText("myre.gretunnel.destiny")));

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
