package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.ComputerSystem;
import org.opennaas.ws.IChassisCapabilityService;
import org.opennaas.ws.IProtocolSessionManagerService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.LogicalPort;
import org.opennaas.ws.NetworkPort;
import org.opennaas.ws.ProtocolException_Exception;
import org.opennaas.ws.ProtocolSessionContext;
import org.opennaas.ws.ProtocolSessionContext.SessionParameters.Entry;
import org.opennaas.ws.ResourceException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class CreateLogicalRoutersAction extends ActionSupport implements SessionAware {

	private Map<String, Object>				session;
	private IChassisCapabilityService		chassisCapability;
	private IResourceManagerService			resourceManagerService;
	private IQueueManagerCapabilityService	queueManager;
	private IProtocolSessionManagerService	protocolSessionManagerService;
	private static final Logger				log	= Logger.getLogger("CreateLogicalRoutersAction.class");

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * shell:echo "CREATE LOGICAL ROUTERS"<br>
	 * ##Creating logical routers<br>
	 * chassis:createlogicalrouter router:heanetM20 logicalheanet1 fe-0/3/3.1 fe-0/3/0.13 ge-0/2/0.80 gr-1/2/0.1 <br>
	 * resource:list <br>
	 * queue:listactions router:heanetM20 <br>
	 * queue:execute router:heanetM20 <br>
	 * protocols:context router:logicalheanet1 netconf ssh://user:password@hea.net:22/netconf <br>
	 * resource:start router:logicalheanet1 <br>
	 * 
	 * chassis:createlogicalrouter router:unicM7i logicalunic1 ge-2/0/0.13 ge-2/0/0.12 ge-2/0/1.81 gr-1/1/0.2 <br>
	 * resource:list <br>
	 * queue:listactions router:unicM7i <br>
	 * queue:execute router:unicM7i <br>
	 * protocols:context router:logicalunic1 netconf ssh://user:password@unic.hea.net:22/netconf <br>
	 * resource:start router:logicalunic1 <br>
	 * 
	 * chassis:createlogicalrouter router:gsnMX10 logicalgsn1 ge-1/0/7.60 ge-1/0/7.59 <br>
	 * queue:listactions router:gsnMX10 <br>
	 * queue:execute router:gsnMX10 <br>
	 * protocols:context router:logicalgsn1 netconf ssh://user:password@gsn.hea.net:22/netconf <br>
	 * resource:start router:logicalgsn1 <br>
	 **/
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		createLogicalRouters();
		return SUCCESS;
	}

	private void createLogicalRouters() throws CapabilityException_Exception, ResourceException_Exception, ActionException_Exception,
			ProtocolException_Exception {
		log.info("createLogicalRouters ...");
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
		log.info("createLogicalRouters done.");

	}

	/**
	 * @return
	 */
	private ProtocolSessionContext getProtocolSessionContext(String protocol, String uri) {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		ProtocolSessionContext.SessionParameters sessionParameters = new ProtocolSessionContext.SessionParameters();
		protocolSessionContext.setSessionParameters(sessionParameters);
		List<Entry> listEntries = sessionParameters.getEntry();
		Entry entry = new Entry();
		entry.setKey("protocol");
		entry.setValue(protocol);
		listEntries.add(entry);
		entry = new Entry();
		entry.setKey("protocol.uri");
		entry.setValue(uri);
		listEntries.add(entry);
		return protocolSessionContext;
	}

	/**
	 * @throws ResourceException_Exception
	 * 
	 */
	private void saveLogicalRoutersInSession() throws ResourceException_Exception {
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

	private ResourceIdentifier getLRResourceId(String logicalRouterName) throws ResourceException_Exception {
		return resourceManagerService.getIdentifierFromResourceName("router", logicalRouterName);
	}
}
