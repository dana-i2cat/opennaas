package org.opennaas.web.actions;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityDescriptor;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.CapabilityProperty;
import org.opennaas.ws.INetworkBasicCapabilityService;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.Information;
import org.opennaas.ws.ResourceDescriptor;
import org.opennaas.ws.ResourceException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class AddResourcesAction extends ActionSupport implements SessionAware {

	private static final long				serialVersionUID	= 1L;
	private Map<String, Object>				session;
	private INetworkBasicCapabilityService	capabilitService;
	private IResourceManagerService			resourceManagerService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Add resources to network
	 */
	@Override
	public String execute() throws Exception {
		addNetworkResources();
		return SUCCESS;
	}

	private void addNetworkResources() throws CapabilityException_Exception, ResourceException_Exception, MalformedURLException {
		capabilitService = OpennaasClient.getNetworkBasicCapabilityService();
		resourceManagerService = OpennaasClient.getResourceManagerService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();
		String lrUnicId = ((ResourceIdentifier) session.get(getText("unic.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();
		String lrGSNId = ((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId();

		// Network
		ResourceIdentifier identifier4 = resourceManagerService
				.createResource(getNetworkResourceDescriptor("", getText("network.name"), "network", ""));
		resourceManagerService.startResource(identifier4);

		session.put(getText("network.name"), identifier4);

		// add logicalUnic
		capabilitService.addResource(networkId, lrUnicId);

		// add logicalmyre
		capabilitService.addResource(networkId, lrMyreId);

		// add logicalGSN
		capabilitService.addResource(networkId, lrGSNId);
	}

	/**
	 * @param description
	 * @param name
	 * @param type
	 * @param version
	 * @return
	 */
	private ResourceDescriptor getNetworkResourceDescriptor(String description, String name, String type, String version) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setInformation(getInformation(name, description, type, version));

		CapabilityDescriptor capabilityDescriptor = getCapabilityDescriptor("Basic Network", "Manages the topology of the Network.", "basicNetwork",
				"network", "1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Network Queue capability", "Manages the queue of all resources of the network.", "netqueue",
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
	private CapabilityDescriptor getCapabilityDescriptor(String name, String description, String type, String actionName, String actionVersion) {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();

		List<CapabilityProperty> listProperties = capabilityDescriptor.getCapabilityProperty();
		listProperties.add(getCapabilityPropery("actionset.name", actionName));
		listProperties.add(getCapabilityPropery("actionset.version", actionVersion));

		capabilityDescriptor.setInformation(getInformation(name, description, type, null));

		return capabilityDescriptor;
	}

	/**
	 * @return
	 */
	private Information getInformation(String name, String description, String type, String version) {
		Information information = new Information();
		information.setDescription(description);
		information.setName(name);
		information.setType(type);
		information.setVersion(version);
		return information;
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

}