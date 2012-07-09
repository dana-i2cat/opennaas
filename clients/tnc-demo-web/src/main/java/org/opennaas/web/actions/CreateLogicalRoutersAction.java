package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.ws.services.IChassisCapabilityService;
import org.opennaas.extensions.ws.services.IProtocolSessionManagerService;
import org.opennaas.extensions.ws.services.IQueueManagerCapabilityService;
import org.opennaas.extensions.ws.services.IResourceManagerService;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class CreateLogicalRoutersAction extends ActionSupport implements SessionAware {

	private static final Logger				LOGGER	= Logger.getLogger(CreateLogicalRoutersAction.class);
	private Map<String, Object>				session;
	private IChassisCapabilityService		chassisCapability;
	private IResourceManagerService			resourceManagerService;
	private IQueueManagerCapabilityService	queueManager;
	private IProtocolSessionManagerService	protocolSessionManagerService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Create Logical Routers
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		createLogicalRouters();
		return SUCCESS;
	}

	/**
	 * @throws ProtocolException
	 * @throws ResourceException
	 */
	private void createLogicalRouters() throws ProtocolException, ResourceException {
		LOGGER.info("createLogicalRouters ...");
		List<String> ifaces = new ArrayList<String>();

		chassisCapability = OpennaasClient.getChassisCapabilityService();
		resourceManagerService = OpennaasClient.getResourceManagerService();
		queueManager = OpennaasClient.getQueueManagerCapabilityService();
		protocolSessionManagerService = OpennaasClient.getProtocolSessionManagerService();

		String routerIdUnic = ((ResourceIdentifier) session.get(getText("unic.router.name"))).getId();
		String routerIdGSN = ((ResourceIdentifier) session.get(getText("gsn.router.name"))).getId();
		String routerIdMyre = ((ResourceIdentifier) session.get(getText("myre.router.name"))).getId();

		chassisCapability.createLogicalRouter(routerIdUnic, getComputerSystem(getText("unic.lrouter.name")));
		ifaces.add(getText("unic.iface1"));
		ifaces.add(getText("unic.iface2"));
		ifaces.add(getText("unic.iface3"));
		chassisCapability.addInterfacesToLogicalRouter(routerIdUnic, getComputerSystem(getText("unic.lrouter.name")),
				getInterfaces(ifaces));
		queueManager.execute(routerIdUnic);

		chassisCapability.createLogicalRouter(routerIdMyre, getComputerSystem(getText("myre.lrouter.name")));
		ifaces.clear();
		ifaces.add(getText("myre.iface1"));
		ifaces.add(getText("myre.iface2"));
		ifaces.add(getText("myre.iface3"));
		ifaces.add(getText("myre.iface.gre"));
		chassisCapability.addInterfacesToLogicalRouter(routerIdMyre, getComputerSystem(getText("myre.lrouter.name")),
				getInterfaces(ifaces));
		queueManager.execute(routerIdMyre);

		chassisCapability.createLogicalRouter(routerIdGSN, getComputerSystem(getText("gsn.lrouter.name")));
		ifaces.clear();
		ifaces.add(getText("gsn.iface1"));
		ifaces.add(getText("gsn.iface2"));
		ifaces.add(getText("gsn.iface3"));
		chassisCapability.addInterfacesToLogicalRouter(routerIdGSN, getComputerSystem(getText("gsn.lrouter.name")), getInterfaces(ifaces));
		queueManager.execute(routerIdGSN);

		saveLogicalRoutersInSession();

		protocolSessionManagerService.registerContext(((ResourceIdentifier) session.get(getText("unic.lrouter.name"))).getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.unic")));
		resourceManagerService.startResource((ResourceIdentifier) session.get(getText("unic.lrouter.name")));

		protocolSessionManagerService.registerContext(((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.myre")));
		resourceManagerService.startResource((ResourceIdentifier) session.get(getText("myre.lrouter.name")));

		protocolSessionManagerService.registerContext(((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.gsn")));
		resourceManagerService.startResource((ResourceIdentifier) session.get(getText("gsn.lrouter.name")));
		LOGGER.info("createLogicalRouters done.");

	}

	/**
	 * @param protocol
	 * @param uri
	 * @return
	 */
	private ProtocolSessionContext getProtocolSessionContext(String protocol, String uri) {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol", protocol);
		protocolSessionContext.addParameter("protocol.uri", uri);
		return protocolSessionContext;
	}

	/**
	 * @throws ResourceException
	 */
	private void saveLogicalRoutersInSession() throws ResourceException {
		ResourceIdentifier lrUnic = getLRResourceId(getText("unic.lrouter.name"));
		ResourceIdentifier lrGSN = getLRResourceId(getText("gsn.lrouter.name"));
		ResourceIdentifier lrMyre = getLRResourceId(getText("myre.lrouter.name"));

		session.put(getText("unic.lrouter.name"), lrUnic);
		session.put(getText("gsn.lrouter.name"), lrGSN);
		session.put(getText("myre.lrouter.name"), lrMyre);
	}

	private List<LogicalPort> getInterfaces(List<String> interfaces) {
		List<LogicalPort> lpList = new ArrayList<LogicalPort>();
		for (String iface : interfaces) {
			NetworkPort np = new NetworkPort();
			String[] argument = iface.split("\\.");
			np.setName(argument[0]);
			np.setPortNumber(Integer.valueOf(argument[1]));
			lpList.add(np);
		}
		return lpList;
	}

	private ComputerSystem getComputerSystem(String name) {
		ComputerSystem router = new ComputerSystem();
		router.setName(name);
		router.setElementName(name);
		return router;
	}

	private ResourceIdentifier getLRResourceId(String logicalRouterName) throws ResourceException {
		return resourceManagerService.getIdentifierFromResourceName("router", logicalRouterName);
	}
}
