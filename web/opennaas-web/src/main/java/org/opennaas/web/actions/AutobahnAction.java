package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.ActionException_Exception;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.IL2BoDCapabilityService;
import org.opennaas.ws.INetworkBasicCapabilityService;
import org.opennaas.ws.IQueueManagerCapabilityService;
import org.opennaas.ws.Interface;
import org.opennaas.ws.ProtocolException_Exception;
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

	public void autobahn() throws CapabilityException_Exception, ResourceException_Exception, ActionException_Exception, ProtocolException_Exception {
		l2BoDCapabilityService = OpennaasClient.getL2BoDCapabilityService();
		queueManager = OpennaasClient.getQueueManagerCapabilityService();

		String autbahnId = ((ResourceIdentifier) session.get(getText("autobahn.bod.name"))).getId();

		// Connection 1
		String interfaceName1 = getText("autobahn.connection1.interface1");
		String interfaceName2 = getText("autobahn.connection1.interface2");
		String vlanid = getText("autobahn.connection1.vlanid");
		String endtime = getText("autobahn.connection1.endtime");
		String capacity = getText("autobahn.connection1.capacity");
		l2BoDCapabilityService.requestConnection(autbahnId, interfaceName1, interfaceName2, vlanid, capacity, endtime);

		queueManager.execute(autbahnId);

		// Connection 2
		interfaceName1 = getText("autobahn.connection2.interface1");
		interfaceName2 = getText("autobahn.connection2.interface2");
		vlanid = getText("autobahn.connection2.vlanid");
		endtime = getText("autobahn.connection2.endtime");
		capacity = getText("autobahn.connection2.capacity");
		l2BoDCapabilityService.requestConnection(autbahnId, interfaceName1, interfaceName2, vlanid, capacity, endtime);

		queueManager.execute(autbahnId);

		// Connection 3
		interfaceName1 = getText("autobahn.connection3.interface1");
		interfaceName2 = getText("autobahn.connection3.interface2");
		vlanid = getText("autobahn.connection3.vlanid");
		endtime = getText("autobahn.connection3.endtime");
		capacity = getText("autobahn.connection3.capacity");
		l2BoDCapabilityService.requestConnection(autbahnId, interfaceName1, interfaceName2, vlanid, capacity, endtime);

		queueManager.execute(autbahnId);
	}

	private void attachNetworkResources() throws CapabilityException_Exception {
		INetworkBasicCapabilityService capabilitService = OpennaasClient.getNetworkBasicCapabilityService();

		String networkId = ((ResourceIdentifier) session.get(getText("network.name"))).getId();

		capabilitService.l2Attach(networkId, getInterface(getText("network.interface.unicmyre")),
				getInterface(getText("network.interface.myreunic")));

		capabilitService.l2Attach(networkId, getInterface(getText("network.interface.unicgsn")),
				getInterface(getText("network.interface.gsnunic")));

		capabilitService.l2Attach(networkId, getInterface(getText("network.interface.myregsn")),
				getInterface(getText("network.interface.gsnmyre")));

	}

	private Interface getInterface(String ifaceName) {
		Interface iface = new Interface();
		iface.setName(ifaceName);
		return iface;
	}
}
