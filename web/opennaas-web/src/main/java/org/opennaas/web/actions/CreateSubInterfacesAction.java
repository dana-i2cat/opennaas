package org.opennaas.web.actions;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.PortImplementsEndpoint;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.ws.services.IChassisCapabilityService;
import org.opennaas.extensions.ws.services.IQueueManagerCapabilityService;
import org.opennaas.web.ws.OpennaasClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class CreateSubInterfacesAction extends ActionSupport implements SessionAware {

	private static final Logger				LOGGER	= Logger.getLogger(CreateSubInterfacesAction.class);
	private Map<String, Object>				session;
	private IQueueManagerCapabilityService	queueManager;
	private IChassisCapabilityService		chassisCapability;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * Create subinterfaces
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		createSubinterfaces();
		return SUCCESS;
	}

	/**
	 * Create interfaces
	 * 
	 * @throws CapabilityException
	 * @throws NumberFormatException
	 * @throws ProtocolException
	 */
	private void createSubinterfaces() throws NumberFormatException, CapabilityException, ProtocolException {
		LOGGER.info("createSubinterfaces ...");
		chassisCapability = OpennaasClient.getChassisCapabilityService();
		queueManager = OpennaasClient.getQueueManagerCapabilityService();

		String routerIdunic = ((ResourceIdentifier) session.get(getText("unic.router.name"))).getId();
		String routerIdGSN = ((ResourceIdentifier) session.get(getText("gsn.router.name"))).getId();
		String routerIdMyre = ((ResourceIdentifier) session.get(getText("myre.router.name"))).getId();

		// unic
		chassisCapability.createSubInterface(routerIdunic, getEthernetPort(getText("unic.iface1"), Integer.valueOf(getText("unic.iface1.vlan"))));
		chassisCapability.createSubInterface(routerIdunic, getEthernetPort(getText("unic.iface2"), Integer.valueOf(getText("unic.iface2.vlan"))));
		chassisCapability.createSubInterface(routerIdunic, getEthernetPort(getText("unic.iface3"), Integer.valueOf(getText("unic.iface3.vlan"))));
		queueManager.execute(routerIdunic);

		// myre
		chassisCapability.createSubInterface(routerIdMyre, getEthernetPort(getText("myre.iface1"), Integer.valueOf(getText("myre.iface1.vlan"))));
		chassisCapability.createSubInterface(routerIdMyre, getEthernetPort(getText("myre.iface2"), Integer.valueOf(getText("myre.iface2.vlan"))));
		chassisCapability.createSubInterface(routerIdMyre, getEthernetPort(getText("myre.iface3"), Integer.valueOf(getText("myre.iface3.vlan"))));
		chassisCapability.createSubInterface(routerIdMyre, getEthernetPort(getText("myre.iface.gre"), Integer.valueOf(getText("myre.iface3.vlan"))));

		queueManager.execute(routerIdMyre);

		// gsn
		chassisCapability.createSubInterface(routerIdGSN, getEthernetPort(getText("gsn.iface1"), Integer.valueOf(getText("gsn.iface1.vlan"))));
		chassisCapability.createSubInterface(routerIdGSN, getEthernetPort(getText("gsn.iface2"), Integer.valueOf(getText("gsn.iface2.vlan"))));
		chassisCapability.createSubInterface(routerIdGSN, getEthernetPort(getText("gsn.iface3"), Integer.valueOf(getText("gsn.iface3.vlan"))));
		queueManager.execute(routerIdGSN);
		LOGGER.info("createSubinterfaces done.");
	}

	/**
	 * @param string
	 * @return
	 */
	private EthernetPort getEthernetPort(String iface, int VLANId) {
		EthernetPort ethPort = new EthernetPort();
		String[] args = iface.split("\\.");

		ethPort.setName(args[0]);
		ethPort.setPortNumber(Integer.parseInt(args[1]));

		VLANEndpoint vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(VLANId);

		PortImplementsEndpoint assoc = new PortImplementsEndpoint();
		assoc.setTo(vlanEndpoint);
		ethPort.getToAssociations().add(assoc);

		return ethPort;
	}
}
