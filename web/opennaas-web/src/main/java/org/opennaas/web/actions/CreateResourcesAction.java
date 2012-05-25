package org.opennaas.web.actions;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityDescriptor;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.CapabilityProperty;
import org.opennaas.ws.IProtocolSessionManagerService;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.Information;
import org.opennaas.ws.ProtocolException_Exception;
import org.opennaas.ws.ProtocolSessionContext;
import org.opennaas.ws.ProtocolSessionContext.SessionParameters.Entry;
import org.opennaas.ws.ResourceDescriptor;
import org.opennaas.ws.ResourceException_Exception;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class CreateResourcesAction extends ActionSupport implements SessionAware {

	private Map<String, Object>				session;
	private IResourceManagerService			resourceManagerService;
	private IProtocolSessionManagerService	protocolSessionManagerService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Create resources
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		createResources();
		return SUCCESS;
	}

	/**
	 * @throws CapabilityException_Exception
	 * @throws ResourceException_Exception
	 * @throws ProtocolException_Exception
	 */
	private void createResources() throws CapabilityException_Exception, ResourceException_Exception, ProtocolException_Exception {
		resourceManagerService = OpennaasClient.getResourceManagerService();
		protocolSessionManagerService = OpennaasClient.getProtocolSessionManagerService();

		// Router 1
		ResourceIdentifier identifier1 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", getText("unic.router.name"), "router", ""));
		protocolSessionManagerService.registerContext(identifier1.getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.unic")));
		resourceManagerService.startResource(identifier1);

		// Router 2
		ResourceIdentifier identifier2 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", getText("gsn.router.name"), "router", ""));
		protocolSessionManagerService.registerContext(identifier2.getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.gsn")));
		resourceManagerService.startResource(identifier2);

		// Router 3
		ResourceIdentifier identifier3 = resourceManagerService
				.createResource(getRouterResourceDescriptor("", getText("myre.router.name"), "router", ""));
		protocolSessionManagerService.registerContext(identifier3.getId(),
				getProtocolSessionContext(getText("protocol.router.name"), getText("protocol.uri.myre")));
		resourceManagerService.startResource(identifier3);

		session.put(getText("unic.router.name"), identifier1);
		session.put(getText("gsn.router.name"), identifier2);
		session.put(getText("myre.router.name"), identifier3);

	}

	/**
	 * @return
	 */
	private ResourceDescriptor getRouterResourceDescriptor(String description, String name, String type, String version) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setInformation(getInformation(name, description, type, version));

		CapabilityDescriptor capabilityDescriptor = getCapabilityDescriptor("IPv4 capability", "IPv4 capability", "ipv4", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Chassis capability", "Chassis capability", "chassis", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("OSPF capability", "OSPF capability", "ospf", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Static Route capability", "Static Route capability", "staticroute", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Queue capability", "Queue capability", "queue", "junos", "10.10");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		if (name.equals(getText("myre.router.name"))) {
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

		capabilityDescriptor.setInformation(getInformation(name, description, type, null));

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
}
