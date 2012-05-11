package org.opennaas.web.actions;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.utils.ResourcesDemo;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityDescriptor;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.CapabilityProperty;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.Information;
import org.opennaas.ws.ResourceDescriptor;
import org.opennaas.ws.ResourceException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class Step1Action extends ActionSupport implements SessionAware {

	private Map<String, Object>	session;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Create resources<br>
	 * <br>
	 * resource:create /home/adrian/heanetM20.descriptor<br>
	 * protocols:context router:heanetM20 netconf ssh://user:password@hea.net:22/netconf<br>
	 * resource:start router:heanetM20<br>
	 * 
	 * ##Creating GSN resource resource:create /home/adrian/gsnMX10.descriptor<br>
	 * protocols:context router:gsnMX10 netconf ssh://user:password@gsn.hea.net:22/netconf<br>
	 * resource:start router:gsnMX10<br>
	 * 
	 * ##Creating UNI-C resource resource:create /home/adrian/unicM7i.descriptor<br>
	 * protocols:context router:unicM7i netconf ssh://user:password@unic.hea.net:22/netconf<br>
	 * resource:start router:unicM7i<br>
	 * 
	 * ##Create demo network resource (with empty topology) resource:create /home/adrian/network.descriptor<br>
	 * resource:start network:networkdemo<br>
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		try {
			createResources();
		} catch (CapabilityException_Exception e) {
			return ERROR;
		} catch (ResourceException_Exception e) {
			return ERROR;
		} catch (Exception e) {
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ResourceException_Exception
	 */
	private void createResources() throws CapabilityException_Exception, ResourceException_Exception {
		IResourceManagerService resourceManagerService = OpennaasClient.getResourceManagerService();
		// Router 1
		ResourceIdentifier identifier1 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", ResourcesDemo.ROUTER_LOLA_NAME, "router", ""));

		// Router 2
		ResourceIdentifier identifier2 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", ResourcesDemo.ROUTER_GSN_NAME, "router", ""));

		// Router 3
		ResourceIdentifier identifier3 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", ResourcesDemo.ROUTER_MYRE_NAME, "router", ""));

		// Network
		ResourceIdentifier identifier4 = resourceManagerService
				.createResource(getNetworkResourceDescriptor("", ResourcesDemo.NETWORK_NAME, "network", ""));

		resourceManagerService.startResource(identifier1);
		resourceManagerService.startResource(identifier2);
		resourceManagerService.startResource(identifier3);
		resourceManagerService.startResource(identifier4);

		session.put(ResourcesDemo.ROUTER_LOLA_NAME, identifier1);
		session.put(ResourcesDemo.ROUTER_GSN_NAME, identifier2);
		session.put(ResourcesDemo.ROUTER_MYRE_NAME, identifier3);
		session.put(ResourcesDemo.NETWORK_NAME, identifier4);
	}

	/**
	 * @param string
	 * @param networkName
	 * @param string2
	 * @param string3
	 * @return
	 */
	private ResourceDescriptor getNetworkResourceDescriptor(String description, String name, String type, String version) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setInformation(getInformation(description, name, type, version));

		CapabilityDescriptor capabilityDescriptor = getCapabilityDescriptor("Basic Network", "Manages the topology of the Network.", "basicNetwork",
				"network", "1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Manages the queue of all resources of the network.", "Network Queue capability", "netqueue",
				"network", "1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Network OSPF capability", "Enables OSPF on all resources of the network", "netospf",
				"network", "1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		return resourceDescriptor;
	}

	/**
	 * @return
	 */
	private ResourceDescriptor getRouterResourceDescriptor(String description, String name, String type, String version) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setInformation(getInformation(description, name, type, version));

		CapabilityDescriptor capabilityDescriptor = getCapabilityDescriptor("IPv4 capability", "IPv4 capability", "ipv4", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Chassis capability", "Chassis capability", "chassis", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("OSPF capability", "OSPF capability", "ospf", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Static Route capability", "Static Route capability", "ipv4", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Queue capability", "Queue capability", "queue", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		if (!name.equals(ResourcesDemo.ROUTER_GSN_NAME)) {
			capabilityDescriptor = getCapabilityDescriptor("GRE capability", "GRE capability", "gretunnel", "junos", "10.10");
			resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);
		}

		return resourceDescriptor;
	}

	/**
	 * @return
	 */
	private CapabilityDescriptor getCapabilityDescriptor(String name, String description, String type, String actionName, String actionVersion) {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		List<CapabilityProperty> listProperties = capabilityDescriptor.getCapabilityProperty();
		listProperties.add(getCapabilityPropery("actionset.name", actionName));
		listProperties.add(getCapabilityPropery("actionset.version", actionVersion));

		capabilityDescriptor.setInformation(getInformation(description, name, type, null));

		return capabilityDescriptor;
	}

	/**
	 * @return
	 */
	private CapabilityProperty getCapabilityPropery(String name, String value) {
		CapabilityProperty capabilityProperty = new CapabilityProperty();
		capabilityProperty.setName(name);
		capabilityProperty.setValue(value);
		return capabilityProperty;
	}

	/**
	 * @return
	 */
	private Information getInformation(String description, String name, String type, String version) {
		Information information = new Information();
		information.setDescription(description);
		information.setName(name);
		information.setType(type);
		information.setVersion(version);
		return information;
	}

}
