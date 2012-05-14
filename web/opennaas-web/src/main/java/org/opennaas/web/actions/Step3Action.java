package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.utils.ResourcesDemo;
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
public class Step3Action extends ActionSupport implements SessionAware {

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
		List<String> ifaces = new ArrayList<String>();

		chassisCapability = OpennaasClient.getChassisCapabilityService();
		resourceManagerService = OpennaasClient.getResourceManagerService();
		queueManager = OpennaasClient.getQueueManagerCapabilityService();
		protocolSessionManagerService = OpennaasClient.getProtocolSessionManagerService();

		String routerIdLola = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_LOLA_NAME)).getId();
		String routerIdGSN = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_GSN_NAME)).getId();
		String routerIdMyre = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_MYRE_NAME)).getId();

		chassisCapability.createLogicalRouter(routerIdLola, getComputerSystem(ResourcesDemo.LOGICAL_LOLA_NAME));
		ifaces.add(ResourcesDemo.LOLA_IFACE1);
		ifaces.add(ResourcesDemo.LOLA_IFACE2);
		ifaces.add(ResourcesDemo.LOLA_IFACE3);
		ifaces.add(ResourcesDemo.LOLA_IFACE_GRE);
		chassisCapability.addInterfacesToLogicalRouter(routerIdLola, getComputerSystem(ResourcesDemo.LOGICAL_LOLA_NAME),
				getInterfaces(ifaces));
		queueManager.execute(routerIdLola);

		chassisCapability.createLogicalRouter(routerIdMyre, getComputerSystem(ResourcesDemo.LOGICAL_MYRE_NAME));
		ifaces.clear();
		ifaces.add(ResourcesDemo.MYRE_IFACE1);
		ifaces.add(ResourcesDemo.MYRE_IFACE2);
		ifaces.add(ResourcesDemo.MYRE_IFACE3);
		ifaces.add(ResourcesDemo.MYRE_IFACE_GRE);
		chassisCapability.addInterfacesToLogicalRouter(routerIdMyre, getComputerSystem(ResourcesDemo.LOGICAL_MYRE_NAME),
				getInterfaces(ifaces));
		queueManager.execute(routerIdMyre);

		chassisCapability.createLogicalRouter(routerIdGSN, getComputerSystem(ResourcesDemo.LOGICAL_GSN_NAME));
		ifaces.clear();
		ifaces.add(ResourcesDemo.GSN_IFACE1);
		ifaces.add(ResourcesDemo.GSN_IFACE2);
		chassisCapability.addInterfacesToLogicalRouter(routerIdGSN, getComputerSystem(ResourcesDemo.LOGICAL_GSN_NAME), getInterfaces(ifaces));
		queueManager.execute(routerIdGSN);

		saveLogicalRoutersInSession();

		protocolSessionManagerService.registerContext(((ResourceIdentifier) session.get(ResourcesDemo.LOGICAL_LOLA_NAME)).getId(),
				getProtocolSessionContext(ResourcesDemo.PROTOCOL_NAME, ResourcesDemo.PROTOCOL_LOLA));
		resourceManagerService.startResource((ResourceIdentifier) session.get(ResourcesDemo.LOGICAL_LOLA_NAME));

		protocolSessionManagerService.registerContext(((ResourceIdentifier) session.get(ResourcesDemo.LOGICAL_MYRE_NAME)).getId(),
				getProtocolSessionContext(ResourcesDemo.PROTOCOL_NAME, ResourcesDemo.PROTOCOL_MYRE));
		resourceManagerService.startResource((ResourceIdentifier) session.get(ResourcesDemo.LOGICAL_MYRE_NAME));

		protocolSessionManagerService.registerContext(((ResourceIdentifier) session.get(ResourcesDemo.LOGICAL_GSN_NAME)).getId(),
				getProtocolSessionContext(ResourcesDemo.PROTOCOL_NAME, ResourcesDemo.PROTOCOL_GSN));
		resourceManagerService.startResource((ResourceIdentifier) session.get(ResourcesDemo.LOGICAL_GSN_NAME));
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
		ResourceIdentifier lrLola = getLRResourceId(ResourcesDemo.LOGICAL_LOLA_NAME);
		ResourceIdentifier lrGSN = getLRResourceId(ResourcesDemo.LOGICAL_GSN_NAME);
		ResourceIdentifier myreGSN = getLRResourceId(ResourcesDemo.LOGICAL_MYRE_NAME);

		session.put(ResourcesDemo.LOGICAL_LOLA_NAME, lrLola);
		session.put(ResourcesDemo.LOGICAL_GSN_NAME, lrGSN);
		session.put(ResourcesDemo.LOGICAL_MYRE_NAME, myreGSN);
	}

	private List<LogicalPort> getInterfaces(List<String> interfaces) {
		List<LogicalPort> lpList = new ArrayList<LogicalPort>();
		for (String iface : interfaces) {
			NetworkPort np = new NetworkPort();
			np.setName(iface);
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
