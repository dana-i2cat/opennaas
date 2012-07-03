package org.opennaas.web.actions;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.ws.services.IL2BoDCapabilityService;
import org.opennaas.extensions.ws.services.INetworkBasicCapabilityService;
import org.opennaas.extensions.ws.services.IProtocolSessionManagerService;
import org.opennaas.extensions.ws.services.IQueueManagerCapabilityService;
import org.opennaas.extensions.ws.services.IResourceManagerService;
import org.opennaas.web.utils.DescriptorUtils;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class AutobahnAction extends ActionSupport implements SessionAware {

	private static final Logger				LOGGER	= Logger.getLogger(AutobahnAction.class);
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
		LOGGER.info("execute ...");
		if (getText("autobahn.enabled").equals("true"))
			autobahn();
		attachNetworkResources();
		LOGGER.info("execute done.");
		return SUCCESS;
	}

	private void autobahn() throws ResourceException, ProtocolException {
		LOGGER.info("autobahn ...");
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
		LOGGER.info("  connection 1 done.");

		// Connection 2
		interfaceName1 = getText("autobahn.connection2.interface1");
		interfaceName2 = getText("autobahn.connection2.interface2");
		vlanid = getText("autobahn.connection2.vlanid");
		endtime = getText("autobahn.connection2.endtime");
		capacity = getText("autobahn.connection2.capacity");

		l2BoDCapabilityService.requestConnection(identifier5.getId(), interfaceName1, interfaceName2, vlanid, capacity, endtime);

		queueManager.execute(identifier5.getId());
		LOGGER.info("  connection 2 done.");
	}

	private void attachNetworkResources() throws CapabilityException {
		LOGGER.info("attachNetworkResources ...");
		networkBasicCapabilityService = OpennaasClient.getNetworkBasicCapabilityService();
		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();

		networkBasicCapabilityService.l2attach(networkId, getInterface(getText("network.interface.unicmyre")),
				getInterface(getText("network.interface.myreunic")));

		networkBasicCapabilityService.l2attach(networkId, getInterface(getText("network.interface.unicgsn")),
				getInterface(getText("network.interface.gsnunic")));

		networkBasicCapabilityService.l2attach(networkId, getInterface(getText("network.interface.myregsn")),
				getInterface(getText("network.interface.gsnmyre")));

		LOGGER.info("attachNetworkResources done.");
	}

	/**
	 * @return
	 */
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
		resourceDescriptor.setInformation(DescriptorUtils.getInformation(name, description, type, version));

		CapabilityDescriptor capabilityDescriptor = DescriptorUtils
				.getCapabilityDescriptor("l2bod capability", "l2bod capability", "l2bod", "autobahn", "1.0");

		resourceDescriptor.setCapabilityDescriptors(new ArrayList<CapabilityDescriptor>());
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

		capabilityDescriptor = DescriptorUtils
				.getCapabilityDescriptor("Queue capability", "Queue capability", "queue", "autobahn", "1.0");
		resourceDescriptor.getCapabilityDescriptors().add(capabilityDescriptor);

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
