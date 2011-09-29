package net.i2cat.mantychore.commandsets.junos.digester;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
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
	String									location		= "";

	VLANEndpoint							vlanEndpoint	= null;
	boolean									flagLT			= false;
	long									peerUnit		= 0;

	public Map<String, OperationalStatus>	mapStatus		= new HashMap<String, OperationalStatus>();

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
			addMyRule("*/interfaces/interface/name", "setLocation", 0);

			/* IP Configuration */
			addObjectCreate("*/interfaces/interface/unit", EthernetPort.class);
			addSetNext("*/interfaces/interface/unit", "addInterface"); /* call our method addInterface!! */
			addMyRule("*/interfaces/interface/unit/name", "setName", 0);
			/* status */
			addMyRule("*/interfaces/interface/disable", "setStatus", 0);

			// addObjectCreate("*/interfaces/interface/unit/peer-unit", LogicalTunnelPort.class);
			addMyRule("*/interfaces/interface/unit/peer-unit", "setPeerUnit", 0);
			addObjectCreate("*/interfaces/interface/unit/family", IPProtocolEndpoint.class);
			addMyRule("*/interfaces/interface/unit/family/inet/address/name", "setIPv4Address", 0);
			addMyRule("*/interfaces/interface/unit/family/inet6/address/name", "setIPv6Address", 0);

			addSetNext("*/interfaces/interface/unit/family", "addProtocolEndpoint");

			addMyRule("*/interfaces/interface/unit/vlan-id", "addVLAN", 0);

			addMyRule("*/interfaces/interface/name", "setLocation", 0);

		}
	}

	public void setStatus(String stat) {
		mapStatus.put(location, OperationalStatus.STOPPED);

	}

	public IPInterfaceParser() {
		ruleSet = new ParserRuleSet();
	}

	public IPInterfaceParser(String prefix) {
		ruleSet = new ParserRuleSet(prefix);
	}

	public void setPeerUnit(String peerunit) {
		this.flagLT = true;
		this.peerUnit = Long.parseLong(peerunit);
	}

	public void addInterface(EthernetPort ethernetPort) {
		String location = ethernetPort.getName() + Integer.toString(ethernetPort.getPortNumber());

		if (flagLT) {
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
			mapElements.put(location, lt);
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
			mapElements.put(location, ethernetPort);
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

	public void setLocation(String location) {
		this.location = location;
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

	public HashMap<String, Object> getMapElements() {
		HashMap<String, Object> mapElements = super.getMapElements();

		/* method to check */
		checkStatus(mapElements);

		return mapElements;
	}

	public void checkStatus(HashMap<String, Object> mapElements) {
		for (String key : mapElements.keySet()) {
			Object element = mapElements.get(key);

			if (element instanceof NetworkPort) {
				NetworkPort networkPort = (NetworkPort) element;
				String name = networkPort.getName(); // always it have one element

				OperationalStatus status = OperationalStatus.OK;
				if (mapStatus.containsKey(name))
					status = mapStatus.get(name);

				networkPort.setOperationalStatus(status);

			}
		}

	}

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
