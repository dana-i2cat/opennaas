package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.INetOSPFCapabilityService;
import org.opennaas.ws.INetQueueCapabilityService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.IStaticRouteCapabilityService;
import org.opennaas.ws.ProtocolException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class SetStaticRouteAction extends ActionSupport implements SessionAware {

	private Map<String, Object>				session;
	private IStaticRouteCapabilityService	staticRouteService;
	private INetOSPFCapabilityService		netOSPFService;
	private INetQueueCapabilityService		netQueueService;
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
	 * #Set static route <br>
	 * staticroute:create router:logicalunic1 0.0.0.0 0.0.0.0 193.1.190.249 <br>
	 * staticroute:create router:logicalmyre1 0.0.0.0 0.0.0.0 193.1.190.2 <br>
	 * queue:execute router:logicalunic1 <br>
	 * queue:execute router:logicalmyre1 <br>
	 * 
	 * shell:echo "CONFIGURE OSPF" <br>
	 * ## Enable ospf in all devices of the network <br>
	 * netospf:activate network:networkdemo <br>
	 * netqueue:execute network:networkdemo <br>
	 **/

	@Override
	public String execute() throws Exception {
		configureStaticRoute();
		configureOSPF();
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

	private void configureOSPF() throws CapabilityException_Exception {
		netOSPFService = OpennaasClient.getNetOSPFCapabilityService();
		netQueueService = OpennaasClient.getNetQueueCapabilityService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
		netOSPFService.activateOSPF(networkId);
		netQueueService.execute(networkId);
	}

}
