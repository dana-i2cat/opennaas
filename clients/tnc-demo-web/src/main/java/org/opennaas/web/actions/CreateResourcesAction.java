package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.ws.services.IProtocolSessionManagerService;
import org.opennaas.extensions.ws.services.IResourceManagerService;
import org.opennaas.web.utils.DescriptorUtils;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * Create resources
 * 
 * @author Jordi
 */
public class CreateResourcesAction extends ActionSupport implements SessionAware {

	private static final Logger				LOGGER				= Logger.getLogger(CreateResourcesAction.class);
	private static final long				serialVersionUID	= 1L;
	private Map<String, Object>				session;
	private IProtocolSessionManagerService	protocolSessionManagerService;
	private IResourceManagerService			resourceManagerService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public String execute() throws Exception {
		createResources();
		return SUCCESS;
	}

	/**
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	private void createResources() throws ResourceException, ProtocolException {
		LOGGER.info("createResources ...");
		resourceManagerService = OpennaasClient.getResourceManagerService();
		protocolSessionManagerService = OpennaasClient.getProtocolSessionManagerService();

		// Router 1
		ResourceIdentifier identifier1 = resourceManagerService.createResource(
				getRouterResourceDescriptor("", getText("unic.router.name"), "router",
						""));
		protocolSessionManagerService.registerContext(identifier1.getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.unic")));
		resourceManagerService.startResource(identifier1);
		LOGGER.info(" resource 1 created!");

		// Router 2
		ResourceIdentifier identifier2 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", getText("gsn.router.name"), "router", ""));
		protocolSessionManagerService.registerContext(identifier2.getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.gsn")));
		resourceManagerService.startResource(identifier2);
		LOGGER.info(" resource 2 created!");

		// Router 3
		ResourceIdentifier identifier3 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", getText("myre.router.name"), "router", ""));
		protocolSessionManagerService.registerContext(identifier3.getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.myre")));
		resourceManagerService.startResource(identifier3);
		LOGGER.info(" resource 3 created!");

		session.put(getText("unic.router.name"), identifier1);
		session.put(getText("gsn.router.name"), identifier2);
		session.put(getText("myre.router.name"), identifier3);

		LOGGER.info("createResources done.");
	}

	/**
	 * @return
	 */
	private ResourceDescriptor getRouterResourceDescriptor(String description, String name, String type, String version) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setInformation(DescriptorUtils.getInformation(name, description, type, version));

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		CapabilityDescriptor capabilityDescriptor = DescriptorUtils.getCapabilityDescriptor("IPv4 capability", "IPv4 capability", "ipv4", "junos",
				"10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = DescriptorUtils.getCapabilityDescriptor("Chassis capability", "Chassis capability", "chassis", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = DescriptorUtils.getCapabilityDescriptor("OSPF capability", "OSPF capability", "ospf", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = DescriptorUtils.getCapabilityDescriptor("Static Route capability", "Static Route capability", "staticroute", "junos",
				"10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = DescriptorUtils.getCapabilityDescriptor("Queue capability", "Queue capability", "queue", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		if (name.equals(getText("myre.router.name"))) {
			capabilityDescriptor = DescriptorUtils.getCapabilityDescriptor("GRE capability", "GRE capability", "gretunnel", "junos", "10.10");
			resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);
		}

		return resourceDescriptor;
	}

	/**
	 * @return
	 */
	private ProtocolSessionContext getProtocolSessionContext(String protocol, String uri) {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol", protocol);
		protocolSessionContext.addParameter("protocol.uri", uri);

		return protocolSessionContext;
	}
}
