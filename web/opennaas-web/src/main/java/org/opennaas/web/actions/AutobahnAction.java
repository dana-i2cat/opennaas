package org.opennaas.web.actions;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityDescriptor;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.CapabilityProperty;
import org.opennaas.ws.IL2BoDCapabilityService;
import org.opennaas.ws.INetworkBasicCapabilityService;
import org.opennaas.ws.IProtocolSessionManagerService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.IResourceManagerService;
import org.opennaas.ws.Information;
import org.opennaas.ws.Interface;
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
public class AutobahnAction extends ActionSupport implements SessionAware {

	private Map<String, Object>				session;
	private IL2BoDCapabilityService			l2BoDCapabilityService;
	private IQueueManagerCapabilityService	queueManager;
	private INetworkBasicCapabilityService	networkBasicCapabilityService;
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
	 * Autobahn dynamic links
	 */

	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		if (getText("autobahn.enabled").equals("true"))
			autobahn();
		attachNetworkResources();
		return SUCCESS;
	}

	private void autobahn() throws CapabilityException_Exception, ResourceException_Exception, ActionException_Exception, ProtocolException_Exception {
		l2BoDCapabilityService = OpennaasClient.getL2BoDCapabilityService();
		queueManager = OpennaasClient.getQueueManagerCapabilityService();
		resourceManagerService = OpennaasClient.getResourceManagerService();
		protocolSessionManagerService = OpennaasClient.getProtocolSessionManagerService();

		// BoD
		ResourceIdentifier identifier5 = null;

		identifier5 = resourceManagerService
				.createResource(getBoDResourceDescriptor("", getText("autobahn.bod.name"), "bod", ""));
		protocolSessionManagerService.registerContext(identifier5.getId(),
				getProtocolSessionContext(getText("protocol.bod.name"), getText("protocol.uri.autobahn")));
		resourceManagerService.startResource(identifier5);

		session.put(getText("autobahn.bod.name"), identifier5);

		// Connection 1
		String interfaceName1 = getText("autobahn.connection1.interface1");
		String interfaceName2 = getText("autobahn.connection1.interface2");
		String vlanid = getText("autobahn.connection1.vlanid");
		String endtime = getText("autobahn.connection1.endtime");
		String capacity = getText("autobahn.connection1.capacity");
		l2BoDCapabilityService.requestConnection(identifier5.getId(), interfaceName1, interfaceName2, vlanid, capacity, endtime);

		queueManager.execute(identifier5.getId());

		// Connection 2
		interfaceName1 = getText("autobahn.connection2.interface1");
		interfaceName2 = getText("autobahn.connection2.interface2");
		vlanid = getText("autobahn.connection2.vlanid");
		endtime = getText("autobahn.connection2.endtime");
		capacity = getText("autobahn.connection2.capacity");
		l2BoDCapabilityService.requestConnection(identifier5.getId(), interfaceName1, interfaceName2, vlanid, capacity, endtime);

		queueManager.execute(identifier5.getId());

		// Connection 3
		// interfaceName1 = getText("autobahn.connection3.interface1");
		// interfaceName2 = getText("autobahn.connection3.interface2");
		// vlanid = getText("autobahn.connection3.vlanid");
		// endtime = getText("autobahn.connection3.endtime");
		// capacity = getText("autobahn.connection3.capacity");
		// l2BoDCapabilityService.requestConnection(autbahnId, interfaceName1, interfaceName2, vlanid, capacity, endtime);
		//
		// queueManager.execute(autbahnId);
	}

	private void attachNetworkResources() throws CapabilityException_Exception {
		networkBasicCapabilityService = OpennaasClient.getNetworkBasicCapabilityService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();

		networkBasicCapabilityService.l2Attach(networkId, getInterface(getText("network.interface.unicmyre")),
				getInterface(getText("network.interface.myreunic")));

		networkBasicCapabilityService.l2Attach(networkId, getInterface(getText("network.interface.unicgsn")),
				getInterface(getText("network.interface.gsnunic")));

		networkBasicCapabilityService.l2Attach(networkId, getInterface(getText("network.interface.myregsn")),
				getInterface(getText("network.interface.gsnmyre")));

	}

	private Interface getInterface(String ifaceName) {
		Interface iface = new Interface();
		iface.setName(ifaceName);
		return iface;
	}

	/**
	 * @param description
	 * @param name
	 * @param type
	 * @param version
	 * @return
	 */
	private ResourceDescriptor getBoDResourceDescriptor(String description, String name, String type, String version) {
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		resourceDescriptor.setInformation(getInformation(name, description, type, version));

		CapabilityDescriptor capabilityDescriptor = getCapabilityDescriptor("l2bod capability", "l2bod capability", "l2bod", "autobahn", "1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = getCapabilityDescriptor("Queue capability", "Queue capability", "queue", "autobahn", "1.0");
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
