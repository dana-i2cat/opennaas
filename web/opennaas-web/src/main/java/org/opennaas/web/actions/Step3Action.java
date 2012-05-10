package org.opennaas.web.actions;

import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.IChassisCapabilityService;
import org.opennaas.ws.NetworkPort;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class Step3Action extends ActionSupport {

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
		IChassisCapabilityService capabilityService = OpennaasClient.getChassisCapabilityService(null);
		capabilityService.createSubInterface("", getNetworkPort());
	}

	/**
	 * @return
	 */
	private NetworkPort getNetworkPort() {
		NetworkPort networkPort = new NetworkPort();
		return null;
	}
}
