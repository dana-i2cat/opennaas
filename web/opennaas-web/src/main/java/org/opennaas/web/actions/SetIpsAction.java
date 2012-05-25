package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.IIPCapabilityService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.IStaticRouteCapabilityService;
import org.opennaas.ws.IpProtocolEndpoint;
import org.opennaas.ws.NetworkPort;
import org.opennaas.ws.ProtocolException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class SetIpsAction extends ActionSupport implements SessionAware {

	private Map<String, Object>				session;
	private IIPCapabilityService			ipCapabilityService;
	private IQueueManagerCapabilityService	queueService;
	private IStaticRouteCapabilityService	staticRouteService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Set Ip Addresses
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		setIPv4();
		configureStaticRoute();
		return SUCCESS;
	}

	private void configureStaticRoute() throws CapabilityException_Exception, ActionException_Exception, ProtocolException_Exception {
		staticRouteService = OpennaasClient.getStaticRouteCapabilityService();
		queueService = OpennaasClient.getQueueManagerCapabilityService();

		String lrGSNId = ((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId();
		String lrUnicId = ((ResourceIdentifier) session.get(getText("unic.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();

		staticRouteService.createStaticRoute(lrUnicId, getText("common.default.route"), getText("common.default.route"),
				getText("unic.staticroute.ip"));
		staticRouteService.createStaticRoute(lrMyreId, getText("common.default.route"), getText("common.default.route"),
				getText("myre.staticroute.ip"));
		staticRouteService.createStaticRoute(lrGSNId, getText("common.default.route"), getText("common.default.route"),
				getText("gsn.staticroute.ip"));

		queueService.execute(lrUnicId);
		queueService.execute(lrMyreId);
		queueService.execute(lrGSNId);
	}

	private void setIPv4() throws CapabilityException_Exception, ActionException_Exception, ProtocolException_Exception {
		String lrUnicId = ((ResourceIdentifier) session.get(getText("unic.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();
		String lrGSNId = ((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId();

		ipCapabilityService = OpennaasClient.getIPCapabilityService();
		IQueueManagerCapabilityService queueManager = OpennaasClient.getQueueManagerCapabilityService();

		// logicalUnic
		ipCapabilityService.setIPv4(lrUnicId, getNetworkPort(getText("unic.iface1")),
				getProtocolEndpoint(getText("unic.iface1.ip"), getText("common.ip.mask")));

		ipCapabilityService.setIPv4(lrUnicId, getNetworkPort(getText("unic.iface2")),
				getProtocolEndpoint(getText("unic.iface2.ip"), getText("common.ip.mask")));

		ipCapabilityService.setIPv4(lrUnicId, getNetworkPort(getText("unic.iface3")),
				getProtocolEndpoint(getText("unic.iface3.ip"), getText("common.ip.mask")));

		// logicalmyre
		ipCapabilityService.setIPv4(lrMyreId, getNetworkPort(getText("myre.iface1")),
				getProtocolEndpoint(getText("myre.iface1.ip"), getText("common.ip.mask")));

		ipCapabilityService.setIPv4(lrMyreId, getNetworkPort(getText("myre.iface2")),
				getProtocolEndpoint(getText("myre.iface2.ip"), getText("common.ip.mask")));

		ipCapabilityService.setIPv4(lrMyreId, getNetworkPort(getText("myre.iface3")),
				getProtocolEndpoint(getText("myre.iface3.ip"), getText("common.ip.mask")));
		// logicalGSN
		ipCapabilityService.setIPv4(lrGSNId, getNetworkPort(getText("gsn.iface1")),
				getProtocolEndpoint(getText("gsn.iface1.ip"), getText("common.ip.mask")));

		ipCapabilityService.setIPv4(lrGSNId, getNetworkPort(getText("gsn.iface2")),
				getProtocolEndpoint(getText("gsn.iface2.ip"), getText("common.ip.mask")));

		ipCapabilityService.setIPv4(lrGSNId, getNetworkPort(getText("gsn.iface3")),
				getProtocolEndpoint(getText("gsn.iface3.ip"), getText("common.ip.mask")));

		queueManager.execute(lrGSNId);
		queueManager.execute(lrMyreId);
		queueManager.execute(lrUnicId);

	}

	/**
	 * @param ip
	 * @param netmask
	 * @return
	 */
	private IpProtocolEndpoint getProtocolEndpoint(String ip, String netmask) {
		IpProtocolEndpoint pE = new IpProtocolEndpoint();
		pE.setIPv4Address(ip);
		pE.setSubnetMask(netmask);
		return pE;
	}

	/**
	 * @param ifaceName
	 * @return
	 */
	private NetworkPort getNetworkPort(String ifaceName) {
		NetworkPort eth = new NetworkPort();
		String[] args = ifaceName.split("\\.");

		eth.setName(args[0]);
		eth.setPortNumber(Integer.valueOf(args[1]));

		return eth;
	}
}
