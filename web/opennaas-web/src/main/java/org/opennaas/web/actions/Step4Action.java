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

		String networkId = ((ResourceIdentifier) session.get(ResourcesDemo.NETWORK_NAME)).getId();

		// add logicalLola
		capabilitService.addResource(networkId, getLogicalRouterId(ResourcesDemo.LOGICAL_LOLA_NAME));

		// add logicalmyre
		capabilitService.addResource(networkId, getLogicalRouterId(ResourcesDemo.LOGICAL_MYRE_NAME));

		// add logicalGSN
		capabilitService.addResource(networkId, getLogicalRouterId(ResourcesDemo.LOGICAL_GSN_NAME));
	}

	private void attachNetworkResources() throws CapabilityException_Exception {
		String networkId = ((ResourceIdentifier) session.get(ResourcesDemo.NETWORK_NAME)).getId();

		capabilitService.l2Attach(networkId, getInterface(ResourcesDemo.NETWORK_INTERFACE_LOLA_MYRE),
				getInterface(ResourcesDemo.NETWORK_INTERFACE_MYRE_LOLA));

		capabilitService.l2Attach(networkId, getInterface(ResourcesDemo.NETWORK_INTERFACE_LOLA_GSN),
				getInterface(ResourcesDemo.NETWORK_INTERFACE_GSN_LOLA));

		capabilitService.l2Attach(networkId, getInterface(ResourcesDemo.NETWORK_INTERFACE_MYRE_GSN),
				getInterface(ResourcesDemo.NETWORK_INTERFACE_GSN_MYRE));

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
