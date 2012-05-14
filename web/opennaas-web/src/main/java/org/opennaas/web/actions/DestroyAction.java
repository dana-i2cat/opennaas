package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.utils.ResourcesDemo;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.ProtocolException_Exception;
import org.opennaas.ws.ResourceException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class DestroyAction extends ActionSupport implements SessionAware {

	private Map<String, Object>		session;
	private IResourceManagerService	resourceManagerService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Destroy
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		removeResources();
		return SUCCESS;
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ResourceException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeResources() throws CapabilityException_Exception, ResourceException_Exception, ProtocolException_Exception {
		resourceManagerService = OpennaasClient.getResourceManagerService();

		ResourceIdentifier routerLola = (ResourceIdentifier) session.get(ResourcesDemo.ROUTER_LOLA_NAME);
		ResourceIdentifier routerGSN = (ResourceIdentifier) session.get(ResourcesDemo.ROUTER_GSN_NAME);
		ResourceIdentifier routerMyre = (ResourceIdentifier) session.get(ResourcesDemo.ROUTER_MYRE_NAME);
		ResourceIdentifier networkDemo = (ResourceIdentifier) session.get(ResourcesDemo.NETWORK_NAME);

		resourceManagerService.stopResource(routerLola);
		resourceManagerService.stopResource(routerGSN);
		resourceManagerService.stopResource(routerMyre);
		resourceManagerService.stopResource(networkDemo);

		resourceManagerService.removeResource(routerLola);
		resourceManagerService.removeResource(routerGSN);
		resourceManagerService.removeResource(routerMyre);
		resourceManagerService.removeResource(networkDemo);
	}

}
