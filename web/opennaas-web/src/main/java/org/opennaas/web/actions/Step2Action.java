package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.utils.ResourcesDemo;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.EthernetPort;
import org.opennaas.ws.IChassisCapabilityService;
import org.opennaas.ws.NetworkPort;
import org.opennaas.ws.PortImplementsEndpoint;
import org.opennaas.ws.ResourceIdentifier;
import org.opennaas.ws.VlanEndpoint;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class Step2Action extends ActionSupport implements SessionAware {

	private Map<String, Object>	session;

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
	public String execute() {
		try {
			createSubinterfaces();
		} catch (CapabilityException_Exception e) {
			return ERROR;
		} catch (Exception e) {
			return ERROR;
		}
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
	 */
	private void createSubinterfaces() throws CapabilityException_Exception {
		IChassisCapabilityService capabilityService = OpennaasClient.getChassisCapabilityService();

		String routerIdLola = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER1_NAME)).getId();
		String routerIdMyre = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER2_NAME)).getId();
		String routerIdGSN = ((ResourceIdentifier) session.get(ResourcesDemo.ROUTER3_NAME)).getId();

		// lola
		capabilityService.createSubInterface(routerIdLola, getNetworkPort("fe-0/3/3.1", 1));
		capabilityService.createSubInterface(routerIdLola, getNetworkPort("fe-0/3/0.13", 13));
		capabilityService.createSubInterface(routerIdLola, getNetworkPort("fe-0/3/0.80", 80));

		// myre
		capabilityService.createSubInterface(routerIdMyre, getNetworkPort("ge-2/0/0.12", 12));
		capabilityService.createSubInterface(routerIdMyre, getNetworkPort("ge-2/0/0.13", 13));
		capabilityService.createSubInterface(routerIdMyre, getNetworkPort("ge-2/0/1.81", 81));

		// gsn
		capabilityService.createSubInterface(routerIdGSN, getNetworkPort("ge-1/0/7.59", 59));
		capabilityService.createSubInterface(routerIdGSN, getNetworkPort("ge-1/0/7.60", 60));

	}

	/**
	 * @param string
	 * @return
	 */
	private NetworkPort getNetworkPort(String iface, int VLANId) {
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
