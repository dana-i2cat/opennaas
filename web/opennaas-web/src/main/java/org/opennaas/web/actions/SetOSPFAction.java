package org.opennaas.web.actions;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.ws.services.INetOSPFCapabilityService;
import org.opennaas.extensions.ws.services.INetQueueCapabilityService;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class SetOSPFAction extends ActionSupport implements SessionAware {

	private static final Logger			LOGGER				= Logger.getLogger(SetIpsAction.class);
	private static final long			serialVersionUID	= 1L;
	private Map<String, Object>			session;

	private INetOSPFCapabilityService	netOSPFService;
	private INetQueueCapabilityService	netQueueService;

	private Boolean						skip;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * @return the skip
	 */
	public Boolean getSkip() {
		return skip;
	}

	/**
	 * @param skip
	 *            the skip to set
	 */
	public void setSkip(Boolean skip) {
		this.skip = skip;
	}

	/**
	 * Set static route and activte OSPF<br>
	 **/
	@Override
	public String execute() throws Exception {
		if (!skip) {
			configureOSPF();
		}
		return SUCCESS;
	}

	private void configureOSPF() throws CapabilityException {
		LOGGER.info("configureOSPF...");
		netOSPFService = OpennaasClient.getNetOSPFCapabilityService();
		netQueueService = OpennaasClient.getNetQueueCapabilityService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
		netOSPFService.activateOSPF(networkId);
		netQueueService.execute(networkId);
		LOGGER.info("configureOSPF done.");
	}

}
