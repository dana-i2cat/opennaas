package org.opennaas.web.actions;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.EthernetPort;
import org.opennaas.ws.IChassisCapabilityService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.PortImplementsEndpoint;
import org.opennaas.ws.ProtocolException_Exception;
import org.opennaas.ws.ResourceIdentifier;
import org.opennaas.ws.VlanEndpoint;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class CreateSubInterfacesAction extends ActionSupport implements SessionAware {

	private Map<String, Object>				session;
	private IQueueManagerCapabilityService	queueManager;
	private IChassisCapabilityService		chassisCapability;
	private static final Logger				log	= Logger.getLogger("CreateSubInterfacesAction.class");

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
	 * ##Create interfaces <br>
	 * <br>
	 * chassis:createsubinterface --vlanid 1 router:heanetM20 fe-0/3/3.1<br>
	 * chassis:createsubinterface --vlanid 13 router:heanetM20 fe-0/3/0.13<br>
	 * chassis:createsubinterface --vlanid 80 router:heanetM20 ge-0/2/0.80<br>
	 * chassis:createsubinterface router:heanetM20 gr-1/2/0.1 <br>
	 * queue:listactions router:heanetM20<br>
	 * queue:execute router:heanetM20<br>
	 * 
	 * chassis:createsubinterface --vlanid 59 router:gsnMX10 ge-1/0/7.59<br>
	 * chassis:createsubinterface --vlanid 60 router:gsnMX10 ge-1/0/7.60<br>
	 * queue:listactions router:gsnMX10<br>
	 * queue:execute router:gsnMX10<br>
	 * 
	 * chassis:createsubinterface --vlanid 81 router:unicM7i ge-2/0/1.81<br>
	 * chassis:createsubinterface --vlanid 12 router:unicM7i ge-2/0/0.12<br>
	 * chassis:createsubinterface --vlanid 13 router:unicM7i ge-2/0/0.13<br>
	 * chassis:createsubinterface router:unicM7i gr-1/1/0.2<br>
	 * queue:listactions router:unicM7i<br>
	 * queue:execute router:unicM7i<br>
	 * 
	 * @throws CapabilityException_Exception
	 * @throws ProtocolException_Exception
	 * @throws ActionException_Exception
	 */
	private void createSubinterfaces() throws CapabilityException_Exception, ActionException_Exception, ProtocolException_Exception {
		log.info("createSubinterfaces ...");
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
		log.info("createSubinterfaces done.");
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

		VlanEndpoint vlanEndpoint = new VlanEndpoint();
		vlanEndpoint.setVlanID(VLANId);

		PortImplementsEndpoint assoc = new PortImplementsEndpoint();
		assoc.setTo(vlanEndpoint);
		ethPort.getToAssociations().add(assoc);

		return ethPort;
	}
}
