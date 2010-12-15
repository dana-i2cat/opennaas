package net.i2cat.mantychore.commandsets.junos.digester;

import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LANEndpoint;
import net.i2cat.mantychore.model.ProtocolEndpoint;

public class LogicalInterfaceParser extends DigesterEngine {
	String	location	= "";

	@Override
	public void addRules() {
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

		/* VLAN configuration */
		addObjectCreate("*/interfaces/interface/unit/family/vlan-id", LANEndpoint.class);
		addBeanPropertySetter("*/interfaces/interface/unit/family/vlan-id", "LANID");
		addSetNext("*/interfaces/interface/unit/family/vlan-id", "addLANEndpoint");

	}

	public void addInterface(EthernetPort ethernetPort) {
		String location = ethernetPort.getOtherPortType();
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
		ethernetPort.setOtherPortType(location + "." + name);

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
			ipProtocolEndpoint.setPrefixLength(Byte.parseByte(shortMask));
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
			// FIXME IT IS NOT POSSIBLE TO SPECIFY MASK?????
			// ipProtocolEndpoint.setPrefixLength(Byte.parseByte(shortMask));

		} catch (Exception e) {
			log.error(e.getMessage());
		}
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
			for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoints()) {
				IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint) protocolEndpoint;
				str += "ipv4: " + ipProtocol.getIPv4Address() + '\n';
				str += "ipv6: " + ipProtocol.getIPv6Address() + '\n';
				for (LANEndpoint lanEndpoint : ipProtocol.getLANEndpoints()) {
					str += "lan id: " + lanEndpoint.getLANID() + '\n';
				}

			}
		}

		return str;

	}

}
