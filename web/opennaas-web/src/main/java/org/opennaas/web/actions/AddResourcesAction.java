package org.opennaas.web.actions;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.INetworkBasicCapabilityService;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class AddResourcesAction extends ActionSupport implements SessionAware {

	private static final long				serialVersionUID	= 1L;
	private Map<String, Object>				session;
	private INetworkBasicCapabilityService	capabilitService;
	private static final Logger				log					= Logger.getLogger(AddResourcesAction.class);

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
		return SUCCESS;
	}

	private void addNetworkResources() throws CapabilityException_Exception {
		log.info("addNetworkResources ...");
		capabilitService = OpennaasClient.getNetworkBasicCapabilityService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
		String lrUnicId = ((ResourceIdentifier) session.get(getText("unic.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();
		String lrGSNId = ((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId();

		// add logicalUnic
		capabilitService.addResource(networkId, lrUnicId);

		// add logicalmyre
		capabilitService.addResource(networkId, lrMyreId);

		// add logicalGSN
		capabilitService.addResource(networkId, lrGSNId);
		log.info("addNetworkResources done.");

	}

}