package net.i2cat.mantychore.commandsets.junos.digester;

import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class IPConfigurationInterfaceParser extends DigesterEngine {
	String			location		= "";

	VLANEndpoint	vlanEndpoint	= null;

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
			addSetNext("*/interfaces/interface/unit", "addInterface");
			addMyRule("*/interfaces/interface/unit/name", "setName", 0);
			addObjectCreate("*/interfaces/interface/unit/family", IPProtocolEndpoint.class);
			addMyRule("*/interfaces/interface/unit/family/inet/address/name", "setIPv4Address", 0);
			addMyRule("*/interfaces/interface/unit/family/inet6/address/name", "setIPv6Address", 0);

			addSetNext("*/interfaces/interface/unit/family", "addProtocolEndpoint");

			addMyRule("*/interfaces/interface/unit/vlan-id", "addVLAN", 0);

		}
	}

	public IPConfigurationInterfaceParser() {
		ruleSet = new ParserRuleSet();
	}

	public IPConfigurationInterfaceParser(String prefix) {
		ruleSet = new ParserRuleSet(prefix);
	}

	public void addInterface(EthernetPort ethernetPort) {
		String location = ethernetPort.getElementName();

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

	/* Configure name */
	public void setName(String name) {
		EthernetPort ethernetPort = (EthernetPort) peek();

		assert !location.equals("") : "LogicalInterfaceParser: location have to be defined";
		/* fill identifier parameters */
		// ethernetPort.setOtherPortType(location + "." + name);
		ethernetPort.setElementName(location);
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
			String ip = ipv6.split("/")[0];
			String shortMask = ipv6.split("/")[1];
			ipProtocolEndpoint.setIPv6Address(ip);
			ipProtocolEndpoint.setPrefixLength(Byte.parseByte(shortMask));

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/* get vlanID */
	public void addVLAN(String vlanID) {
		vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(Integer.parseInt(vlanID));

	}

	public String toPrint() {

		String str = "" + '\n';
		for (String key : this.mapElements.keySet()) {
			EthernetPort port = (EthernetPort) mapElements.get(key);
			str += "- EthernetPort: " + '\n';
			str += port.getOtherPortType() + '\n';
			str += port.getPermanentAddress() + '\n';
			str += String.valueOf(port.isFullDuplex()) + '\n';
			str += String.valueOf(port.getMaxSpeed()) + '\n';
			str += port.getDescription() + '\n';
			for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
				IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint)
						protocolEndpoint;
				str += "ipv4: " + ipProtocol.getIPv4Address() + '\n';
				str += "ipv6: " + ipProtocol.getIPv6Address() + '\n';
			}
		}

		return str;

	}

}
