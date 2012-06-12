package org.opennaas.web.actions;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.GRETunnelServiceConfiguration;
import org.opennaas.extensions.router.model.ProvidesEndpoint;
import org.opennaas.extensions.ws.services.IGRETunnelCapabilityService;
import org.opennaas.extensions.ws.services.IQueueManagerCapabilityService;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class ConfigureGRETunnelAction extends ActionSupport implements SessionAware {

	private static final Logger				LOGGER	= Logger.getLogger(ConfigureGRETunnelAction.class);
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

	private void configureGRE() throws CapabilityException, ProtocolException {
		LOGGER.info("configureGRE ...");
		greTunnelService = OpennaasClient.getGreTunnelCapabilityService();

		queueService = OpennaasClient.getQueueManagerCapabilityService();

		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();

		greTunnelService.createGRETunnel(
				lrMyreId,
				getGRETunnel(getText("myre.iface.gre"), getText("myre.gretunnel.ip"), getText("common.ip.mask"), getText("myre.iface1.ip"),
						getText("myre.gretunnel.destiny")));

		queueService.execute(lrMyreId);
		LOGGER.info("configureGRE done.");
	}

	private GRETunnelService getGRETunnel(String interfaceName, String tunnelIP, String tunnelMask, String ipSource, String ipDestiny) {
		LOGGER.info("getGRETunnel ...");
		GRETunnelService greService = new GRETunnelService();
		greService.setName(interfaceName);

		GRETunnelConfiguration greConfig = new GRETunnelConfiguration();
		greConfig.setSourceAddress(ipSource);
		greConfig.setDestinationAddress(ipDestiny);

		GRETunnelEndpoint greTunnelEndpoint = new GRETunnelEndpoint();
		greTunnelEndpoint.setIPv4Address(tunnelIP);
		greTunnelEndpoint.setSubnetMask(tunnelMask);

		// associate GRETunnelService and GRETunnelConfiguration
		GRETunnelServiceConfiguration assoc = new GRETunnelServiceConfiguration();
		assoc.setTo(greConfig);
		greService.getToAssociations().add(assoc);

		// associate GRETunnelService and GRETunnelEndpoint
		ProvidesEndpoint assoc2 = new ProvidesEndpoint();
		assoc2.setTo(greTunnelEndpoint);
		greService.getToAssociations().add(assoc2);

		LOGGER.info("getGRETunnel done.");
		return greService;
	}
}
