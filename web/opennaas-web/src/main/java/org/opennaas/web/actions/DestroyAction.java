package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.ComputerSystem;
import org.opennaas.ws.GreTunnelService;
import org.opennaas.ws.IChassisCapabilityService;
import org.opennaas.ws.IGRETunnelCapabilityService;
import org.opennaas.ws.IL2BoDCapabilityService;
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
		if (getText("autobahn.enabled").equals("true"))
			shutDownAutobahn();
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

		ResourceIdentifier lrUnic = (ResourceIdentifier) session.get(getText("unic.lrouter.name"));
		ResourceIdentifier lrGSN = (ResourceIdentifier) session.get(getText("myre.lrouter.name"));
		ResourceIdentifier lrMyre = (ResourceIdentifier) session.get(getText("gsn.lrouter.name"));

		resourceManagerService.stopResource(lrUnic);
		resourceManagerService.stopResource(lrMyre);
		resourceManagerService.stopResource(lrGSN);

		resourceManagerService.removeResourceById(lrUnic.getId());
		resourceManagerService.removeResourceById(lrMyre.getId());
		resourceManagerService.removeResourceById(lrGSN.getId());

		String routerUnicId = ((ResourceIdentifier) session.get(getText("unic.router.name"))).getId();
		String routerMyreId = ((ResourceIdentifier) session.get(getText("myre.router.name"))).getId();
		String routerGSNId = ((ResourceIdentifier) session.get(getText("gsn.router.name"))).getId();

		IChassisCapabilityService chassisCapab = OpennaasClient.getChassisCapabilityService();

		ComputerSystem router = new ComputerSystem();
		router.setName(getText("unic.lrouter.name"));
		chassisCapab.deleteLogicalRouter(routerUnicId, router);
		router.setName(getText("myre.lrouter.name"));
		chassisCapab.deleteLogicalRouter(routerMyreId, router);
		router.setName(getText("gsn.lrouter.name"));
		chassisCapab.deleteLogicalRouter(routerGSNId, router);

		IQueueManagerCapabilityService queueCapab = OpennaasClient.getQueueManagerCapabilityService();
		queueCapab.execute(routerUnicId);
		queueCapab.execute(routerMyreId);
		queueCapab.execute(routerGSNId);
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ActionException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeGRE() throws CapabilityException_Exception, ActionException_Exception, ProtocolException_Exception {
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();

		GreTunnelService greTunnelService = new GreTunnelService();

		IGRETunnelCapabilityService greCapab = OpennaasClient.getGRETunnelCapabilityService();

		greTunnelService.setName(getText("myre.iface.gre"));
		greCapab.deleteGRETunnel(lrMyreId, greTunnelService);

		IQueueManagerCapabilityService queueCapab = OpennaasClient.getQueueManagerCapabilityService();
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
	 * Shutdown autobahn connections
	 * 
	 * @throws CapabilityException_Exception
	 */
	private void shutDownAutobahn() throws CapabilityException_Exception, ResourceException_Exception {
		IL2BoDCapabilityService l2BoDCapabilityService = OpennaasClient.getL2BoDCapabilityService();
		String autbahnId = ((ResourceIdentifier) session.get(getText("autobahn.bod.name"))).getId();

		List<String> list1 = new ArrayList<String>();
		list1.add(getText("autobahn.connection1.interface1"));
		list1.add(getText("autobahn.connection1.interface2"));
		l2BoDCapabilityService.shutDownConnection(autbahnId, list1);

		List<String> list2 = new ArrayList<String>();
		list2.add(getText("autobahn.connection2.interface1"));
		list2.add(getText("autobahn.connection2.interface2"));
		l2BoDCapabilityService.shutDownConnection(autbahnId, list2);

		List<String> list3 = new ArrayList<String>();
		list3.add(getText("autobahn.connection3.interface1"));
		list3.add(getText("autobahn.connection3.interface2"));
		l2BoDCapabilityService.shutDownConnection(autbahnId, list3);
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ResourceException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void removeResources() throws CapabilityException_Exception, ResourceException_Exception, ProtocolException_Exception {
		resourceManagerService = OpennaasClient.getResourceManagerService();

		ResourceIdentifier routerUnic = (ResourceIdentifier) session.get(getText("unic.router.name"));
		ResourceIdentifier routerGSN = (ResourceIdentifier) session.get(getText("gsn.router.name"));
		ResourceIdentifier routerMyre = (ResourceIdentifier) session.get(getText("myre.router.name"));
		ResourceIdentifier networkDemo = (ResourceIdentifier) session.get(getText("network.name"));

		resourceManagerService.stopResource(routerUnic);
		resourceManagerService.stopResource(routerGSN);
		resourceManagerService.stopResource(routerMyre);
		resourceManagerService.stopResource(networkDemo);

		resourceManagerService.removeResourceById(routerUnic.getId());
		resourceManagerService.removeResourceById(routerGSN.getId());
		resourceManagerService.removeResourceById(routerMyre.getId());
		resourceManagerService.removeResourceById(networkDemo.getId());
	}

}
