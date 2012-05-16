package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.INetworkBasicCapabilityService;
import org.opennaas.ws.Interface;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class AddResourcesAction extends ActionSupport implements SessionAware {

	private static final long				serialVersionUID	= 1L;
	private Map<String, Object>				session;
	private INetworkBasicCapabilityService	capabilitService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Add resources to network
	 */
	@Override
	public String execute() throws Exception {
		addNetworkResources();
		attachNetworkResources();
		return SUCCESS;
	}

	private void addNetworkResources() throws CapabilityException_Exception {
		capabilitService = OpennaasClient.getNetworkBasicCapabilityService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
		String lrLolaId = ((ResourceIdentifier) session.get(getText("lola.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();
		String lrGSNId = ((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId();

		// add logicalLola
		capabilitService.addResource(networkId, lrLolaId);

		// add logicalmyre
		capabilitService.addResource(networkId, lrMyreId);

		// add logicalGSN
		capabilitService.addResource(networkId, lrGSNId);
	}

	private void attachNetworkResources() throws CapabilityException_Exception {
		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();

		capabilitService.l2Attach(networkId, getInterface(getText("network.interface.lolamyre")),
				getInterface(getText("network.interface.myrelola")));

		capabilitService.l2Attach(networkId, getInterface(getText("network.interface.lolagsn")),
				getInterface(getText("network.interface.gsnlola")));

		capabilitService.l2Attach(networkId, getInterface(getText("network.interface.myregsn")),
				getInterface(getText("network.interface.gsnmyre")));

	}

	private Interface getInterface(String ifaceName) {
		Interface iface = new Interface();
		iface.setName(ifaceName);
		return null;
	}

}
