package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.INetOSPFCapabilityService;
import org.opennaas.ws.INetQueueCapabilityService;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class SetOSPFAction extends ActionSupport implements SessionAware {

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

	private static final long	serialVersionUID	= 1L;

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

	private void configureOSPF() throws CapabilityException_Exception {
		netOSPFService = OpennaasClient.getNetOSPFCapabilityService();
		netQueueService = OpennaasClient.getNetQueueCapabilityService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
		netOSPFService.activateOSPF(networkId);
		netQueueService.execute(networkId);
	}

}
