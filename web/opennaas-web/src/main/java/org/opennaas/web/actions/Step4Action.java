package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.utils.ResourcesDemo;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.INetworkBasicCapabilityService;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.Interface;
import org.opennaas.ws.ResourceException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class Step4Action extends ActionSupport implements SessionAware {

	private static final long				serialVersionUID	= 1L;
	private Map<String, Object>				session;
	private IResourceManagerService			resourceManagerService;
	private INetworkBasicCapabilityService	capabilitService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * shell:echo "ADD RESOURCES TO NETWORK" <br>
	 * ##Add resources to network <br>
	 * net:addResource network:networkdemo router:logicallola1 <br>
	 * net:addResource network:networkdemo router:logicalmyre1 <br>
	 * net:addresource network:networkdemo router:logicalgsn1 <br>
	 * 
	 * shell:echo "UPDATE NETWORK TOPOLOGY WITH EXISTENT LINKS" <br>
	 * ##Tell network who is connected with who unsing only static vlans <br>
	 * net:l2attach network:networkdemo router:logicallola1:fe-0/3/0.13 router:logicalmyre1:ge-2/0/0.13 <br>
	 * net:l2attach network:networkdemo router:logicallola1:ge-0/2/0.80 router:logicalgsn1:ge-1/0/7.59 <br>
	 * net:l2attach network:networkdemo router:logicalgsn1:ge-1/0/7.60 router:logicalmyre1:ge-2/0/1.81 <br>
	 */
	@Override
	public String execute() throws Exception {
		try {
			addNetworkResources();
			attachNetworkResources();
			return SUCCESS;
		} catch (CapabilityException_Exception e) {
			return ERROR;
		} catch (Exception e) {
			return ERROR;
		}
	}

	private void addNetworkResources() throws CapabilityException_Exception, ResourceException_Exception {
		resourceManagerService = OpennaasClient.getResourceManagerService();
		capabilitService = OpennaasClient.getNetworkBasicCapabilityService();

		String routerIdLola = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_LOLA_NAME)).getId();
		String routerIdMyre = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_GSN_NAME)).getId();
		String routerIdGSN = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER_MYRE_NAME)).getId();

		// add logicalLola

		capabilitService.addResource(routerIdLola, getLogicalRouterId(ResourcesDemo.LOGICAL_LOLA_NAME));

		// add logicalmyre
		capabilitService.addResource(routerIdMyre, getLogicalRouterId(ResourcesDemo.LOGICAL_MYRE_NAME));

		// add logicalGSN
		capabilitService.addResource(routerIdGSN, getLogicalRouterId(ResourcesDemo.LOGICAL_GSN_NAME));
	}

	private void attachNetworkResources() throws CapabilityException_Exception {

		String iface1 = "router:logicallola1:fe-0/3/0.13";
		String iface2 = "router:logicalmyre1:ge-2/0/0.13";
		capabilitService.l2Attach("", getInterface(iface1), getInterface(iface2));

		iface1 = "router:logicallola1:ge-0/2/0.80";
		iface2 = "router:logicalgsn1:ge-1/0/7.59";
		capabilitService.l2Attach("", getInterface(iface1), getInterface(iface2));

		iface1 = "router:logicalgsn1:ge-1/0/7.60";
		iface2 = "router:logicalmyre1:ge-2/0/1.81";
		capabilitService.l2Attach("", getInterface(iface1), getInterface(iface2));

	}

	private Interface getInterface(String ifaceName) {
		Interface iface = new Interface();
		iface.setName(ifaceName);
		return null;
	}

	private String getLogicalRouterId(String logicalRouterName) throws ResourceException_Exception {
		ResourceIdentifier resourceIdentifier = resourceManagerService.getIdentifierFromResourceName("router", logicalRouterName);
		return resourceIdentifier.getId();
	}

}
