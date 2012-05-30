package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.extensions.ws.services.INetworkBasicCapabilityService;
import org.opennaas.extensions.ws.services.IResourceManagerService;
import org.opennaas.web.utils.DescriptorUtils;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class AddResourcesAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 1L;
	private static final Logger				LOGGER				= Logger.getLogger(AddResourcesAction.class);
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

	private void addNetworkResources() throws ResourceException {
		LOGGER.info("addNetworkResources ...");
		capabilitService = OpennaasClient.getNetworkBasicCapabilityService();
		resourceManagerService = OpennaasClient.getResourceManagerService();

		String lrUnicId = ((ResourceIdentifier) session.get(getText("unic.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();
		String lrGSNId = ((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId();

		// Network
		ResourceIdentifier identifier4 = resourceManagerService
				.createResource(getNetworkResourceDescriptor("", getText("network.name"), "network", ""));
		resourceManagerService.startResource(identifier4);

		session.put(getText("network.name"), identifier4);

		// add logicalUnic
		capabilitService.addResource(identifier4.getId(), lrUnicId);

		// add logicalmyre
		capabilitService.addResource(identifier4.getId(), lrMyreId);

		// add logicalGSN
		capabilitService.addResource(identifier4.getId(), lrGSNId);

		LOGGER.info("addNetworkResources done.");
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
		resourceDescriptor.setInformation(DescriptorUtils.getInformation(name, description, type, version));

		CapabilityDescriptor capabilityDescriptor = DescriptorUtils
				.getCapabilityDescriptor("Basic Network", "Manages the topology of the Network.", "basicNetwork", "network", "1.0");

		resourceDescriptor.setCapabilityDescriptors(new ArrayList<CapabilityDescriptor>());
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = DescriptorUtils
				.getCapabilityDescriptor("Network Queue capability", "Manages the queue of all resources of the network.", "netqueue", "network",
						"1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = DescriptorUtils
				.getCapabilityDescriptor("Network OSPF capability", "Enables OSPF on all resources of the network", "netospf", "network", "1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		return resourceDescriptor;
	}
}