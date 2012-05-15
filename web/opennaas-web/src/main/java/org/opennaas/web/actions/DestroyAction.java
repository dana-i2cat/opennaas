package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.ComputerSystem;
import org.opennaas.ws.GreTunnelService;
import org.opennaas.ws.IChassisCapabilityService;
import org.opennaas.ws.IGRETunnelCapabilityService;
import org.opennaas.ws.INetOSPFCapabilityService;
import org.opennaas.ws.INetQueueCapabilityService;
import org.opennaas.ws.IQueueManagerCapabilityService;
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
		deactivateOSPF();
		removeGRE();
		removeLR();
		removeResources();
		removeSession();
		return SUCCESS;
	}

	/**
	 * 
	 */
	private void removeSession() {
		session.clear();
	}

	/**
	 * @throws ResourceException_Exception
	 * @throws CapabilityException_Exception
	 * @throws ActionException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeLR() throws ResourceException_Exception, CapabilityException_Exception, ActionException_Exception, ProtocolException_Exception {
		resourceManagerService = OpennaasClient.getResourceManagerService();

		ResourceIdentifier lrLola = (ResourceIdentifier) session.get(getText("lola.lrouter.name"));
		ResourceIdentifier lrGSN = (ResourceIdentifier) session.get(getText("myre.lrouter.name"));
		ResourceIdentifier lrMyre = (ResourceIdentifier) session.get(getText("gsn.lrouter.name"));

		resourceManagerService.stopResource(lrLola);
		resourceManagerService.stopResource(lrMyre);
		resourceManagerService.stopResource(lrGSN);

		resourceManagerService.removeResource(lrLola);
		resourceManagerService.removeResource(lrMyre);
		resourceManagerService.removeResource(lrGSN);

		String routerLolaId = ((ResourceIdentifier) session.get(getText("lola.router.name"))).getId();
		String routerMyreId = ((ResourceIdentifier) session.get(getText("myre.router.name"))).getId();
		String routerGSNId = ((ResourceIdentifier) session.get(getText("gsn.router.name"))).getId();

		IChassisCapabilityService chassisCapab = OpennaasClient.getChassisCapabilityService();

		ComputerSystem router = new ComputerSystem();
		router.setName(getText("lola.lrouter.name"));
		chassisCapab.deleteLogicalRouter(routerLolaId, router);
		router.setName(getText("myre.lrouter.name"));
		chassisCapab.deleteLogicalRouter(routerMyreId, router);
		router.setName(getText("gsn.lrouter.name"));
		chassisCapab.deleteLogicalRouter(routerGSNId, router);

		IQueueManagerCapabilityService queueCapab = OpennaasClient.getQueueManagerCapabilityService();
		queueCapab.execute(routerLolaId);
		queueCapab.execute(routerMyreId);
		queueCapab.execute(routerGSNId);
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ActionException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeGRE() throws CapabilityException_Exception, ActionException_Exception, ProtocolException_Exception {
		String lrLolaId = ((ResourceIdentifier) session.get(getText("lola.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();

		GreTunnelService greTunnelService = new GreTunnelService();

		IGRETunnelCapabilityService greCapab = OpennaasClient.getGRETunnelCapabilityService();
		greTunnelService.setName(getText("lola.iface.gre"));
		greCapab.deleteGRETunnel(lrLolaId, greTunnelService);

		greTunnelService.setName(getText("myre.iface.gre"));
		greCapab.deleteGRETunnel(lrMyreId, greTunnelService);

		IQueueManagerCapabilityService queueCapab = OpennaasClient.getQueueManagerCapabilityService();
		queueCapab.execute(lrLolaId);
		queueCapab.execute(lrMyreId);
	}

	/**
	 * @throws CapabilityException_Exception
	 */
	private void deactivateOSPF() throws CapabilityException_Exception {
		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
		INetOSPFCapabilityService capabilityService = OpennaasClient.getNetOSPFCapabilityService();
		capabilityService.deactivateOSPF(networkId);

		INetQueueCapabilityService queueService = OpennaasClient.getNetQueueCapabilityService();
		queueService.execute(networkId);
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ResourceException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeResources() throws CapabilityException_Exception, ResourceException_Exception, ProtocolException_Exception {
		resourceManagerService = OpennaasClient.getResourceManagerService();

		ResourceIdentifier routerLola = (ResourceIdentifier) session.get(getText("lola.router.name"));
		ResourceIdentifier routerGSN = (ResourceIdentifier) session.get(getText("gsn.router.name"));
		ResourceIdentifier routerMyre = (ResourceIdentifier) session.get(getText("myre.router.name"));
		ResourceIdentifier networkDemo = (ResourceIdentifier) session.get(getText("network.name"));

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
