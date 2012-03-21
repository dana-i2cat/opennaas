package net.i2cat.mantychore.commandsets.junos.digester;

import java.util.ArrayList;
import java.util.HashMap;

import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.System;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * 
 * Parser of the interfaces. Takes the name and unit of the interface. Set IP values, VLAN and peer-unit when exists
 * 
 * @author Evelyn Torras
 * 
 */
public class IPInterfaceParser extends DigesterEngine {

	private System				model;

	String						location				= "";

	VLANEndpoint				vlanEndpoint			= null;
	boolean						flagLT					= false;

	GRETunnelConfiguration		gretunnelConfiguration	= null;
	boolean						flagGT					= false;

	long						peerUnit				= 0;

	public ArrayList<String>	disableInterface		= new ArrayList<String>();

	/** vlan info **/

	class ParserRuleSet extends RuleSetBase {
		private String	prefix	= "";

		protected ParserRuleSet() {

		}

		protected ParserRuleSet(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public void addRuleInstances(Digester arg0) {
			// FIXME IT HAVE TO GET ONLY PHYSICAL INTERFACES
			addMyRule("*/interfaces/interface/", "setOperationalStatus", 0);
			addMyRule("*/interfaces/interface/name", "setLocation", 0);

			/* IP Configuration */
			addObjectCreate("*/interfaces/interface/unit", EthernetPort.class);
			addSetNext("*/interfaces/interface/unit", "addInterface"); /* call our method addInterface!! */
			addMyRule("*/interfaces/interface/unit/name", "setName", 0);
			addMyRule("*/interfaces/interface/unit/description", "setDescription", 0);
			/* status */
			addMyRule("*/interfaces/interface/disable", "setStatus", 0);

			// addObjectCreate("*/interfaces/interface/unit/peer-unit", LogicalTunnelPort.class);
			addMyRule("*/interfaces/interface/unit/peer-unit", "setPeerUnit", 0);
			addObjectCreate("*/interfaces/interface/unit/family", IPProtocolEndpoint.class);
			addMyRule("*/interfaces/interface/unit/family/inet/address/name", "setIPv4Address", 0);
			addMyRule("*/interfaces/interface/unit/family/inet6/address/name", "setIPv6Address", 0);
			addSetNext("*/interfaces/interface/unit/family", "addProtocolEndpoint");

			/* GRETunnel Configuration */
			addMyRule("*/interfaces/interface/unit/tunnel", "setGRETunnel", 0);
			addMyRule("*/interfaces/interface/unit/tunnel/source", "setSourceAddress", 0);
			addMyRule("*/interfaces/interface/unit/tunnel/destination", "setDestinationAddress", 0);
			addMyRule("*/interfaces/interface/unit/tunnel/key", "setGRETunnelKey", 0);

			addMyRule("*/interfaces/interface/unit/vlan-id", "addVLAN", 0);

			addMyRule("*/interfaces/interface/name", "setLocation", 0);

		}
	}

	public IPInterfaceParser(System routerModel) {
		ruleSet = new ParserRuleSet();
		setModel(routerModel);
	}

	public IPInterfaceParser() {
		ruleSet = new ParserRuleSet();
	}

	public void setOperationalStatus(String s) {
		System routerModel = this.model;
		for (LogicalDevice port : routerModel.getLogicalDevices()) {
			if (disableInterface.contains(port.getName())) {
				port.setOperationalStatus(OperationalStatus.STOPPED);
			} else {
				port.setOperationalStatus(OperationalStatus.OK);
			}
		}
	}

	public System getModel() {
		return model;
	}

	public void setModel(System model) {
		this.model = model;
	}

	public void setStatus(String e) {
		disableInterface.add(location);
	}

	public IPInterfaceParser(String prefix) {
		ruleSet = new ParserRuleSet(prefix);
	}

	public void setPeerUnit(String peerunit) {
		this.flagLT = true;
		this.peerUnit = Long.parseLong(peerunit);
	}

	public void setGRETunnel(Object object) {
		this.flagGT = true;
	}

	public void addInterface(EthernetPort ethernetPort) {
		String location = ethernetPort.getName() + Integer.toString(ethernetPort.getPortNumber());

		if (flagGT) {
			addGreTunnel(ethernetPort);
			flagGT = false;
			gretunnelConfiguration = null;
		}
		else if (flagLT) {
			LogicalTunnelPort lt = new LogicalTunnelPort();
			lt.setName(ethernetPort.getName());
			lt.setPortNumber(ethernetPort.getPortNumber());
			lt.setPeer_unit(peerUnit);
			/* set status */
			lt.setOperationalStatus(ethernetPort.getOperationalStatus());
			for (ProtocolEndpoint pE : ethernetPort.getProtocolEndpoint()) {
				lt.addProtocolEndpoint(pE);
			}
			if (vlanEndpoint != null) {
				lt.addProtocolEndpoint(vlanEndpoint);
				// setPortImplementsVlan(vlanEndpoint);
				vlanEndpoint = null;
			}
			model.addLogicalDevice(lt);
			// mapElements.put(location, lt);
			flagLT = false;
			peerUnit = 0;
		} else {
			/* add new vlan endpoint */
			if (vlanEndpoint != null) {
				ethernetPort.addProtocolEndpoint(vlanEndpoint);
				// setPortImplementsVlan(vlanEndpoint);
				vlanEndpoint = null;
			}

			if (mapElements.containsKey(location)) {
				EthernetPort hashEthernetPort = (EthernetPort) mapElements.get(location);
				ethernetPort.merge(hashEthernetPort);
				mapElements.remove(location);
			}
			// mapElements.put(location, ethernetPort);
			model.addLogicalDevice(ethernetPort);
		}

	}

	private void addGreTunnel(EthernetPort ethernetPort) {

		if (gretunnelConfiguration != null) {
			GRETunnelService gretunnelService = new GRETunnelService();
			gretunnelService.setName(ethernetPort.getName() + '.' + ethernetPort.getPortNumber());
			gretunnelService.setGRETunnelConfiguration(gretunnelConfiguration);
			for (ProtocolEndpoint pE : ethernetPort.getProtocolEndpoint()) {

				if (pE instanceof IPProtocolEndpoint) {

					IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) pE;
					GRETunnelEndpoint gretunnelEndpoint = new GRETunnelEndpoint();
					String ip = ipProtocolEndpoint.getIPv4Address();
					if (ip != null) {
						gretunnelEndpoint.setIPv4Address(ip);
					} else {
						ip = ipProtocolEndpoint.getIPv6Address();
						gretunnelEndpoint.setIPv6Address(ip);
					}
					gretunnelEndpoint.setSubnetMask(ipProtocolEndpoint.getSubnetMask());

					gretunnelService.addProtocolEndpoint(gretunnelEndpoint);
				}
			}

			model.addHostedService(gretunnelService);
		}
	}

	/* Configure name */
	public void setName(String name) {
		NetworkPort ethernetPort = (NetworkPort) peek();

		assert !location.equals("") : "LogicalInterfaceParser: location have to be defined";
		/* fill identifier parameters */
		// ethernetPort.setOtherPortType(location + "." + name);
		ethernetPort.setName(location);
		ethernetPort.setPortNumber(Integer.parseInt(name));
	}

	public void setDescription(String description) {
		NetworkPort ethernetPort = (NetworkPort) peek();
		ethernetPort.setDescription(description);
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/* GRETunnel Endpoint */
	public void setSourceAddress(String ip) {
		if (gretunnelConfiguration == null)
			gretunnelConfiguration = new GRETunnelConfiguration();
		ip = ip.split("/")[0];
		gretunnelConfiguration.setSourceAddress(ip);

	}

	public void setDestinationAddress(String ip) {
		if (gretunnelConfiguration == null)
			gretunnelConfiguration = new GRETunnelConfiguration();
		ip = ip.split("/")[0];
		gretunnelConfiguration.setDestinationAddress(ip);
	}

	public void setGRETunnelKey(String key) {
		if (gretunnelConfiguration == null)
			gretunnelConfiguration = new GRETunnelConfiguration();
		int new_key = Integer.parseInt(key);
		gretunnelConfiguration.setKey(new_key);
	}

	/* IP Protocol Endpoint */
	public void setIPv4Address(String ipv4) {
		IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) peek();
		try {
			String shortMask = ipv4.split("/")[1];
			String ip = ipv4.split("/")[0];
			String maskIpv4 = IPUtilsHelper.parseShortToLongIpv4NetMask(shortMask);
			ipProtocolEndpoint.setIPv4Address(ip);
			ipProtocolEndpoint.setSubnetMask(maskIpv4);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public void setIPv6Address(String ipv6) {
		IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) peek();
		try {
			// TODO implement a method to convert the mask of an IPv6 address
			String ip = ipv6.split("/")[0];
			String shortMask = ipv6.split("/")[1];
			// ipProtocolEndpoint.setIPv6Address(ip);
			// ipProtocolEndpoint.setPrefixLength(Byte.parseByte(shortMask));

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/* get vlanID */
	public void addVLAN(String vlanID) {
		vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(Integer.parseInt(vlanID));

	}

	@Deprecated
	public String toPrint() {

		HashMap<String, Object> mapElements = this.getMapElements();
		String str = "" + '\n';
		for (String key : mapElements.keySet()) {
			NetworkPort port = (NetworkPort) mapElements.get(key);
			str += "- EthernetPort: " + '\n';
			str += port.getName() + '\n';
			str += port.getPermanentAddress() + '\n';
			str += String.valueOf(port.getMaxSpeed()) + '\n';
			str += port.getDescription() + '\n';
			// str += port.getOperationalStatus().toString() + '\n';
			if (port instanceof LogicalTunnelPort) {
				str += ((LogicalTunnelPort) port).getPeer_unit() + '\n';
			}

			for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
				if (protocolEndpoint instanceof IPProtocolEndpoint) {
					IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint)
							protocolEndpoint;
					str += "ipv4: " + ipProtocol.getIPv4Address() + '\n';
					str += "ipv6: " + ipProtocol.getIPv6Address() + '\n';
				} else if (protocolEndpoint instanceof VLANEndpoint) {
					VLANEndpoint vlanEndpoint = (VLANEndpoint) protocolEndpoint;
					str += "vlan: " + vlanEndpoint.getVlanID() + '\n';
				}
			}
		}

		return str;

	}

}