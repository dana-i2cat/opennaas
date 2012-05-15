package org.opennaas.web.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.ws.OpennaasClient;
import org.opennaas.ws.CapabilityException_Exception;
import org.opennaas.ws.IIPCapabilityService;
import org.opennaas.ws.IpProtocolEndpoint;
import org.opennaas.ws.LogicalDevice;
import org.opennaas.ws.LogicalPort;
import org.opennaas.ws.ResourceIdentifier;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class SetIpsAction extends ActionSupport implements SessionAware {

	private Map<String, Object>	session;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	/**
	 * shell:echo "SET IP ADDRESSES" <br>
	 * ##Set ip addresses on router interfaces <br>
	 * ipv4:setip router:logicallola1 ge-0/2/0.80 192.168.1.5 255.255.255.252 <br>
	 * ipv4:setip router:logicalgsn1 ge-1/0/7.59 192.168.1.6 255.255.255.252 <br>
	 * ipv4:setip router:logicalgsn1 ge-1/0/7.60 192.168.1.9 255.255.255.252 <br>
	 * ipv4:setip router:logicalmyre1 ge-2/0/1.81 192.168.1.10 255.255.255.252 <br>
	 * ipv4:setip router:logicalmyre1 ge-2/0/0.13 192.168.1.13 255.255.255.252 <br>
	 * ipv4:setip router:logicallola1 fe-0/3/0.13 192.168.1.14 255.255.255.252 <br>
	 * ipv4:setip router:logicallola1 fe-0/3/3.1 193.1.190.250 255.255.255.252 <br>
	 * ipv4:setip router:logicalmyre1 ge-2/0/0.12 193.1.190.1 255.255.255.252 <br>
	 * queue:execute router:logicallola1 <br>
	 * queue:execute router:logicalmyre1 <br>
	 * queue:execute router:logicalgsn1 <br>
	 */

	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {

		setIPv4();
		return SUCCESS;
	}

	public void setIPv4() throws CapabilityException_Exception {
		String lrLolaId = ((ResourceIdentifier) session.get(getText("lola.lrouter.name"))).getId();
		String lrMyreId = ((ResourceIdentifier) session.get(getText("myre.lrouter.name"))).getId();
		String lrGSNId = ((ResourceIdentifier) session.get(getText("gsn.lrouter.name"))).getId();

		IIPCapabilityService capabilityService = OpennaasClient.getIPCapabilityService();

		// logicalLola
		capabilityService.setIPv4(lrLolaId, getLogicalDevice(getText("lola.iface1")),
				getProtocolEndpoint(getText("lola.iface1.ip"), getText("common.ip.mask")));

		capabilityService.setIPv4(lrLolaId, getLogicalDevice(getText("lola.iface2")),
				getProtocolEndpoint(getText("lola.iface2.ip"), getText("common.ip.mask")));

		capabilityService.setIPv4(lrLolaId, getLogicalDevice(getText("lola.iface3")),
				getProtocolEndpoint(getText("lola.iface3.ip"), getText("common.ip.mask")));

		// logicalmyre
		capabilityService.setIPv4(lrMyreId, getLogicalDevice(getText("myre.iface1")),
				getProtocolEndpoint(getText("myre.iface1.ip"), getText("common.ip.mask")));

		capabilityService.setIPv4(lrMyreId, getLogicalDevice(getText("myre.iface2")),
				getProtocolEndpoint(getText("myre.iface2.ip"), getText("common.ip.mask")));

		capabilityService.setIPv4(lrMyreId, getLogicalDevice(getText("myre.iface3")),
				getProtocolEndpoint(getText("myre.iface3.ip"), getText("common.ip.mask")));

		// logicalGSN
		capabilityService.setIPv4(lrGSNId, getLogicalDevice(getText("gsn.iface1")),
				getProtocolEndpoint(getText("gsn.iface1.ip"), getText("common.ip.mask")));

		capabilityService.setIPv4(lrGSNId, getLogicalDevice(getText("gsn.iface2")),
				getProtocolEndpoint(getText("gsn.iface2.ip"), getText("common.ip.mask")));
	}

	private IpProtocolEndpoint getProtocolEndpoint(String ip, String netmask) {
		IpProtocolEndpoint pE = new IpProtocolEndpoint();
		pE.setIPv4Address(ip);
		pE.setSubnetMask(netmask);
		return pE;
	}

	private LogicalDevice getLogicalDevice(String ifaceName) {
		LogicalPort lp = new LogicalPort();
		lp.setName(ifaceName);

		return lp;
	}
}
